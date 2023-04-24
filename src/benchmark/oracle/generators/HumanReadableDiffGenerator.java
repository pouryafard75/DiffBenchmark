package benchmark.oracle.generators;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.AdditionalASTConstants;
import benchmark.utils.PathResolver;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.Tree;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static benchmark.oracle.generators.GeneratorUtils.*;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.*;

/* Created by pourya on 2023-04-02 9:24 p.m. */
public class HumanReadableDiffGenerator {
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
    public HumanReadableDiffGenerator(String repo, String commit, ASTDiff astDiff) {
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
        make();
    }
    private void make() {
        newPopulation();
    }

    private void addAccordingly(AbstractMapping abstractMapping) {
        //TODO MAKE IT WORK FOR DIFFERENT TYPES
        if (!abstractMapping.getLeftType().equals(abstractMapping.getRightType()))
            return;
//            throw new RuntimeException("Different AST TYPES!");
        String leftType = abstractMapping.getLeftType();
        if (leftType.equals(Constants.TYPE_DECLARATION) ||
            leftType.equals(Constants.METHOD_DECLARATION) ||
            leftType.equals(Constants.FIELD_DECLARATION))
            result.getMatchedElements().add(abstractMapping);
        else
            result.getMappings().add(abstractMapping);
    }
    private void newPopulation() {
        for (Mapping mapping : mappings) {
            if (isInterFileMapping(mapping,src,dst)) continue;
            if (isPartOfJavadoc(mapping)) continue;
            if (isBetweenDifferentTypes(mapping)) continue;
//                throw new RuntimeException("Different AST TYPES!");
            String firstType = mapping.first.getType().name; //Second type definitely has the same type due to the previous check
            switch (firstType)
            {
                case Constants.TYPE_DECLARATION:
                    addAccordingly(new AbstractMapping(mapping,generateClassSignature(mapping.first),generateClassSignature(mapping.second)));
                    handleTypeDeclaration(mapping);
                    break;
                case Constants.METHOD_DECLARATION:
                    addAccordingly(new AbstractMapping(mapping,generateMethodSignature(mapping.first),generateMethodSignature(mapping.second)));
                    handleMethodDeclaration(mapping);
                    break;
                case Constants.FIELD_DECLARATION:
                    addAccordingly(new AbstractMapping(mapping,generateFieldSignature(mapping.first),generateFieldSignature(mapping.second)));
                    handleFieldDeclaration(mapping);
                    break;
            }
            handleStatement(mapping);
            handleMove(mapping);
            //TODO MUST FIX THE ISSUE RELATED TO MULTI-MAPPINGS WITH UPDATE-MOVE ON TOP
            handleUpdate(mapping);
        }
    }
    private void handleUpdate(Mapping mapping) {
        /*Update case*/
        if (!mapping.first.getLabel().equals(mapping.second.getLabel()))
            addAccordingly(getAbstractMapping(mapping, srcContent, dstContent));
    }
    private void handleMove(Mapping mapping) {
        if (mapping.first.getParent() == null || mapping.second.getParent() == null) return;
        Tree firstParent = mapping.first.getParent();
        Tree secondParent = mapping.second.getParent();
        if (mappings.getDsts(firstParent) == null)
            /*Source's parent is not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcContent, dstContent));
        else if (!mappings.getDsts(firstParent).contains(secondParent))
            /*Parents are not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcContent, dstContent));
    }
    private void handleFieldDeclaration(Mapping mapping) {
        //TODO: MUST BE IMPLEMENTED ASAP
    }
    private void handleMethodDeclaration(Mapping mapping) {
        for (Mapping signatureMapping : getMethodSignatureMappings(mapping, mappings)) {
            addAccordingly(getAbstractMapping(signatureMapping, srcContent, dstContent));
        }
    }
    private void handleTypeDeclaration(Mapping mapping) {
        for (Mapping signatureMapping : getClassSignatureMappings(mapping, mappings)) {
            addAccordingly(getAbstractMapping(signatureMapping, srcContent, dstContent));
        }
    }
    private void handleStatement(Mapping mapping) {
        if (isStatement(mapping.first.getType().name))
            addAccordingly(getAbstractMapping(mapping, srcContent, dstContent));
    }
    public static boolean isBetweenDifferentTypes(Mapping mapping) {
        return !mapping.first.getType().name.equals(mapping.second.getType().name);
    }
    public static boolean isPartOfJavadoc(Mapping mapping) {
        if (isPartOfJavadoc(mapping.first) || isPartOfJavadoc(mapping.second))
            return true;
        return false;
    }
    public static boolean isPartOfJavadoc(Tree srcSubTree) {
        if (srcSubTree.getType().name.equals(AdditionalASTConstants.JAVA_DOC))
            return true;
        if (srcSubTree.getParent() == null) return false;
        return isPartOfJavadoc(srcSubTree.getParent());
    }
    public static boolean isInterFileMapping(Mapping mapping, Tree src, Tree dst) {
        if (mapping.first.getParent() != null & mapping.second.getParents() != null)
        {
            Tree srcLast = getParentUntilType(mapping.first,AdditionalASTConstants.COMPILATION_UNIT);
            Tree dstLast = getParentUntilType(mapping.second,AdditionalASTConstants.COMPILATION_UNIT);
            if (!srcLast.equals(src) || !dstLast.equals(dst))
                return true;
        }
        return false;
    }

    public static Set<Mapping> getClassSignatureMappings(Mapping mapping, ExtendedMultiMappingStore mappings) {
        Set<Mapping> abstractMappingSet = new LinkedHashSet<>();
        boolean _met = false;
        for (Tree child : mapping.first.getChildren()) {
            if (isPartOfJavadoc(child)) continue;
            if (child.getType().name.equals(Constants.SIMPLE_NAME))
                _met = true;
            if (_met && !child.getType().name.equals(AdditionalASTConstants.SIMPLE_TYPE))
                break;
            Set<Tree> dsts = mappings.getDsts(child);
            if (dsts == null) continue;
            for (Tree mappedDst : dsts) {
                abstractMappingSet.add(new Mapping(child, mappedDst));
            }
        }
        return abstractMappingSet;
    }
    public static Set<Mapping> getMethodSignatureMappings(Mapping mapping, ExtendedMultiMappingStore mappings) {
        Set<Mapping> abstractMappingSet = new LinkedHashSet<>();
        for (Tree child : mapping.first.getChildren()) {
            if (child.getType().name.equals(Constants.BLOCK)) continue;
            if (isPartOfJavadoc(child)) continue;
            Set<Tree> dsts = mappings.getDsts(child);
            if (dsts == null) continue;
            for (Tree mappedDst : dsts) {
                abstractMappingSet.add(new Mapping(child, mappedDst));
            }
        }
        return abstractMappingSet;
    }
    private static AbstractMapping getAbstractMapping(Mapping mapping, String srcContent, String dstContent) {
        String srcString = getString(mapping.first, srcContent);
        String dstString = getString(mapping.second, dstContent);
        return new AbstractMapping(mapping, srcString, dstString);
    }
    public static String getString(Tree tree, String content) {

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
        try{
            content.substring(start, end);
        }
        catch (Exception e)
        {
            System.out.println("Error");
        }
        return content.substring(start, end);
    }
    public void write(String folderName) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter("    ", "\n"));
        try {
            String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            out = prettify(out);
            FileUtils.writeStringToFile(new File(PathResolver.getFinalPath(folderName, srcPath, commit, repo)), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prettify(String out) {
        return out.replace(": [", ": [\n");
    }
}


