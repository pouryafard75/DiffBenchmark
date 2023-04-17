package benchmark.Oracle;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static benchmark.Oracle.BenchmarkUtils.*;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.*;

/* Created by pourya on 2023-04-02 9:24 p.m. */
public class OracleGenerator {
    private final String repo;
    private final String commit;
    private final Tree src;
    private final Tree dst;
    private final ExtendedMultiMappingStore mappings;
    private final String srcPath;
    private final String dstPath;
    private final String srcContent;
    private final String dstContent;
    public HumanReadableDiff result;

    Comparator<AbstractMapping> abstractMappingComparator = Comparator.comparing(AbstractMapping::getLeftOffset)
            .thenComparing(AbstractMapping::getRightOffset)
            .thenComparing(AbstractMapping::getLeftEndOffset)
            .thenComparing(AbstractMapping::getRightEndOffset);
    public OracleGenerator(String repo, String commit, ASTDiff astDiff) {
        this.repo = repo;
        this.commit = commit;
        this.src = astDiff.src.getRoot();
        this.dst = astDiff.dst.getRoot();
        this.srcPath = astDiff.getSrcPath();
        this.dstPath = astDiff.getDstPath();
        this.srcContent = astDiff.getSrcContents();
        this.dstContent = astDiff.getDstContents();
        this.mappings = astDiff.getMultiMappings();
        result = new HumanReadableDiff();
    }


    public HumanReadableDiff make() {
        populate();
        return result;
    }
    private void populate(){
        populateAbstractMappingList();
        populateMatchedProgramElements();
    }

    private void populateMatchedProgramElements() {
        List<AbstractMapping> matchedProgramElements = new ArrayList<>();
        for (Mapping mapping : mappings) {
            String firstType = mapping.first.getType().name;
            switch (firstType)
            {
                case Constants.TYPE_DECLARATION:
                    matchedProgramElements.add(new AbstractMapping(mapping,generateClassSignature(mapping.first),generateClassSignature(mapping.second)));
                    break;
                case Constants.METHOD_DECLARATION:
                    matchedProgramElements.add(new AbstractMapping(mapping,generateMethodSignature(mapping.first),generateMethodSignature(mapping.second)));
                    break;
                case Constants.FIELD_DECLARATION:
                    matchedProgramElements.add(new AbstractMapping(mapping,generateFieldSignature(mapping.first),generateFieldSignature(mapping.second)));
                    break;
            }
        }
        matchedProgramElements.sort(abstractMappingComparator);
        result.setMatchedElements(matchedProgramElements);
    }

    private void populateAbstractMappingList() {
        List<AbstractMapping> abstractMappingList = new ArrayList<>();
        for (Mapping mapping : mappings) {
            if (mapping.first.getParent() != null & mapping.second.getParents() != null)
            {
                Tree srcLast = getParentUntilType(mapping.first,"CompilationUnit");
                if (!srcLast.equals(src))
                    continue;
                Tree dstLast = getParentUntilType(mapping.second,"CompilationUnit");
                if (!dstLast.equals(dst))
                    continue;
            }
            Tree first = mapping.first;
            Tree second = mapping.second;
            if (isPartOfJavadoc(first)) continue;
            if (isStatement(first.getType().name))
            {
                String srcString = getString(first, srcContent);
                String dstString = getString(second, dstContent);
                abstractMappingList.add(new AbstractMapping(mapping,srcString,dstString));
            }
            else{
                if (first.getParent() == null || second.getParent() == null) continue;
                Tree firstParent = first.getParent();
                Tree secondParent = second.getParent();
                if (mappings.getDsts(firstParent) == null)
                {
                    String srcString = getString(first, srcContent);
                    String dstString = getString(second, dstContent);
                    abstractMappingList.add(new AbstractMapping(mapping,srcString,dstString));
                }
                else if (!mappings.getDsts(firstParent).contains(secondParent))
                {
                    String srcString = getString(first, srcContent);
                    String dstString = getString(second, dstContent);
                    abstractMappingList.add(new AbstractMapping(mapping,srcString,dstString));
                }
            }
        }

        abstractMappingList.sort(abstractMappingComparator);

        result.mappings = abstractMappingList;
    }

    private boolean isPartOfJavadoc(Tree srcSubTree) {
        if (srcSubTree.getType().name.equals("JavaDoc"))
            return true;
        if (srcSubTree.getParent() == null) return false;
        return isPartOfJavadoc(srcSubTree.getParent());
    }

    public static String getString(Tree tree, String content) {
//        if (true)
//            return content.substring(tree.getPos(),tree.getEndPos());

        int start = tree.getPos();
        int end = tree.getEndPos();

        String type = tree.getType().name;
        Tree founded = getTreeBetweenPositions(tree, start, end, type);
        int endoff = tree.getEndPos();
        Tree prevChild = tree;
        boolean _flag = false;
        switch (type)
        {
            case Constants.BLOCK:
                return "{}";
            case Constants.IF_STATEMENT:

                Tree child = founded.getChild(0);
                return content.substring(start,child.getEndPos()) + ")";
            case Constants.ENHANCED_FOR_STATEMENT:
            case Constants.FOR_STATEMENT:
            case Constants.WHILE_STATEMENT:
                endoff = founded.getEndPos();
                for (Tree foundedChild : founded.getChildren()) {
                    if (foundedChild.getType().name.equals(Constants.BLOCK))
                    {
                        endoff = prevChild.getEndPos();
                        _flag = true;
                        break;
                    }
                    prevChild = foundedChild;
                }
                String ret = content.substring(start, endoff);
                if (_flag) ret += ")";
                return ret;
            case Constants.TRY_STATEMENT:
                return "try{...}";
            case Constants.DO_STATEMENT:
                return "do{...}";
        }
        return content.substring(start, end);
    }

    public void write(String folderName){
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter("    ", "\n"));
        try {
            String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            FileUtils.writeStringToFile(new File(getFinalPath(folderName, srcPath, commit, repo)), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getFinalPath(String folderName, String fileName, String commit, String repo) {
        String replacedFileName = replaceFileName(fileName);
        String repoReplaced = repoFolder(repo);
        return folderName + repoReplaced + "/" + commit + "/" + replacedFileName;
    }

    public static String replaceFileName(String srcPath) {
        return srcPath.replace("/", ".").replace(".java", ".json");
    }

    public static String repoFolder(String repo) {
        return repo.replace("https://github.com/", "").replace(".git", "").replace("/", ".");
    }
}

class HumanReadableDiff implements Serializable
{
    List<AbstractMapping> matchedElements;
    List<AbstractMapping> mappings;

    public HumanReadableDiff(List<AbstractMapping> matchedElements, List<AbstractMapping> mappings) {
        this.matchedElements = matchedElements;
        this.mappings = mappings;
    }

    public HumanReadableDiff() {
        
    }

    public List<AbstractMapping> getMatchedElements() {

        return matchedElements;
    }

    public void setMatchedElements(List<AbstractMapping> matchedElements) {
        this.matchedElements = matchedElements;
    }

    public List<AbstractMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<AbstractMapping> mappings) {
        this.mappings = mappings;
    }
}


