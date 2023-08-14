package benchmark.oracle.generators;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import benchmark.utils.PathResolver;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.TreeAddition;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.SimplifiedChawatheScriptGenerator;
import org.refactoringminer.astDiff.actions.model.MoveIn;
import org.refactoringminer.astDiff.actions.model.MoveOut;
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
    private final Map<String, String> fileContentsBefore;
    private final Map<String, String> fileContentsCurrent;
    private final ASTDiff astDiff;
    public HumanReadableDiff result;
    public HumanReadableDiffGenerator(String repo, String commit, ASTDiff astDiff, Map<String, String> fileContentsBefore, Map<String, String> fileContentsCurrent) throws Exception {
        this.repo = repo;
        this.commit = commit;
//        this.src = astDiff.src.getRoot();
//        this.dst = astDiff.dst.getRoot();
//        this.mappings = astDiff.getAllMappings();
//        this.es = astDiff.editScript;
        this.astDiff = astDiff;

        this.fileContentsBefore = fileContentsBefore;
        this.fileContentsCurrent = fileContentsCurrent;
        result = new HumanReadableDiff();
        make();
    }
    private void make() throws Exception {
        newPopulation();
    }

    private void addAccordingly(AbstractMapping abstractMapping, NecessaryMappings target) {
        //TODO MAKE IT WORK FOR DIFFERENT TYPES
        if (!abstractMapping.getLeftType().equals(abstractMapping.getRightType()))
            return;
//            throw new RuntimeException("Different AST TYPES!");
        String leftType = abstractMapping.getLeftType();
        if (leftType.equals(Constants.TYPE_DECLARATION) ||
            leftType.equals(Constants.METHOD_DECLARATION) ||
            leftType.equals(Constants.FIELD_DECLARATION))
            target.getMatchedElements().add(abstractMapping);
        else
            target.getMappings().add(abstractMapping);
    }
    private void newPopulation() throws Exception {
        String currSrc = "";
        String currDst = "";
        Tree src = astDiff.src.getRoot();
        Tree dst = astDiff.dst.getRoot();
        NecessaryMappings target = null;
        boolean _inter = false;
        for (Mapping mapping : astDiff.getAllMappings()) {
            if (isPartOfJavadoc(mapping)) continue;
            if (isBetweenDifferentTypes(mapping)) continue;
            String firstType = mapping.first.getType().name; //Second type definitely has the same type due to the previous check
            if (isInterFileMapping(mapping,src,dst))
            {
                _inter = true;
                boolean _found = false;
                if (firstType.equals(Constants.TYPE_DECLARATION) ||
                        firstType.equals(Constants.METHOD_DECLARATION) ||
                        firstType.equals(Constants.FIELD_DECLARATION) || isStatement(firstType)) {
                    for (Action e : astDiff.editScript) {
                        if (e instanceof MoveOut || e instanceof MoveIn) {
                            if (e.getNode().toTreeString().equals(mapping.first.toTreeString()) ||
                                    e.getNode().toTreeString().equals(mapping.second.toTreeString()) ||
                                    ((TreeAddition) e).getParent().toTreeString().equals(mapping.first.toTreeString()) ||
                                    ((TreeAddition) e).getParent().toTreeString().equals(mapping.second.toTreeString()))
                            {
                                String key = e.toString();
                                result.interfileMappings.putIfAbsent(key, new NecessaryMappings());
                                target = result.interfileMappings.get(key);
                                if (e instanceof MoveIn)
                                {
                                    currSrc = ((MoveIn) e).getSrcFile();
                                    currDst = astDiff.getDstPath();
                                }
                                else {
                                    currSrc = astDiff.getSrcPath();
                                    currDst = ((MoveOut) e).getDstFile();
                                }
                                _found = true;
                                break;
                            }
                        }
                    }
                    if (!_found)
                    {
                        continue;
                    }
                }
            }
            else {
                target = result.fileMappings;
                currSrc = astDiff.getSrcPath();
                currDst = astDiff.getDstPath();
            }
            switch (firstType)
            {
                case Constants.TYPE_DECLARATION:
                    addAccordingly(new AbstractMapping(mapping,generateClassSignature(mapping.first),generateClassSignature(mapping.second)), target);
                    handleTypeDeclaration(mapping,target,currSrc,currDst);
                    break;
                case Constants.METHOD_DECLARATION:
                    addAccordingly(new AbstractMapping(mapping,generateMethodSignature(mapping.first),generateMethodSignature(mapping.second)), target);
                    handleMethodDeclaration(mapping,target,currSrc,currDst);
                    break;
                case Constants.FIELD_DECLARATION:
                    addAccordingly(new AbstractMapping(mapping,generateFieldSignature(mapping.first),generateFieldSignature(mapping.second)), target);
                    handleFieldDeclaration(mapping,target,currSrc,currDst);
                    break;
            }
            handleStatement(mapping, target,currSrc,currDst);
            handleMove(mapping, target,currSrc,currDst);
            //TODO MUST FIX THE ISSUE RELATED TO MULTI-MAPPINGS WITH UPDATE-MOVE ON TOP
            handleUpdate(mapping, target,currSrc,currDst);
        }
    }
    private void handleUpdate(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        /*Update case*/
        if (!mapping.first.getLabel().equals(mapping.second.getLabel()))
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
    }
    private void handleMove(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        if (mapping.first.getParent() == null || mapping.second.getParent() == null) return;
        Tree firstParent = mapping.first.getParent();
        Tree secondParent = mapping.second.getParent();
        if (astDiff.getAllMappings().getDsts(firstParent) == null)
            /*Source's parent is not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
        else if (!astDiff.getAllMappings().getDsts(firstParent).contains(secondParent))
            /*Parents are not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
    }
    private void handleFieldDeclaration(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        //TODO: MUST BE IMPLEMENTED ASAP
    }
    private void handleMethodDeclaration(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        for (Mapping signatureMapping : getMethodSignatureMappings(mapping, astDiff.getAllMappings())) {
            addAccordingly(getAbstractMapping(signatureMapping, srcPath, dstPath), target);
        }
    }
    private void handleTypeDeclaration(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        for (Mapping signatureMapping : getClassSignatureMappings(mapping, astDiff.getAllMappings())) {
            addAccordingly(getAbstractMapping(signatureMapping, srcPath, dstPath), target);
        }
    }
    private void handleStatement(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        if (isStatement(mapping.first.getType().name))
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
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
        if (srcSubTree.getType().name.equals(Constants.JAVA_DOC))
            return true;
        if (srcSubTree.getParent() == null) return false;
        return isPartOfJavadoc(srcSubTree.getParent());
    }
    public static boolean isInterFileMapping(Mapping mapping, Tree src, Tree dst) {
        if (mapping.first.getParent() != null & mapping.second.getParents() != null)
        {
            Tree srcLast = getParentUntilType(mapping.first,Constants.COMPILATION_UNIT);
            Tree dstLast = getParentUntilType(mapping.second,Constants.COMPILATION_UNIT);
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
            if (_met && !child.getType().name.equals(Constants.SIMPLE_TYPE))
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
    private AbstractMapping getAbstractMapping(Mapping mapping, String srcPath, String dstPath) {
        String srcContent = getSrcContent(srcPath);
        String dstContent = getDstContent(dstPath);
        String srcString = "",dstString = "";
//        if (!isInterFileMapping(mapping,src,dst)) { //TODO:
        if (true) {
            srcString = getString(mapping.first, srcContent);
            dstString = getString(mapping.second, dstContent);
        }
        return new AbstractMapping(mapping, srcString, dstString);
    }

    private String getDstContent(String dstPath) {
        return fileContentsCurrent.get(dstPath);
    }

    private String getSrcContent(String srcPath) {
        return fileContentsBefore.get(srcPath);
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
        String res = "";
        try{
        res = content.substring(start, end);
        }
        catch (Exception e)
        {
            System.out.println();
        }
        return res;

    }
    public void write(String folderName, String srcPath) {
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


