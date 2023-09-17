package benchmark.oracle.generators;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Compatibility;
import benchmark.utils.Configuration;
import benchmark.utils.PathResolver;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;


import java.io.File;
import java.util.*;

import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.*;

/* Created by pourya on 2023-04-02 9:24 p.m. */
public abstract class HumanReadableDiffGenerator {
    private final String repo;
    private final String commit;
    private final Map<String, String> fileContentsBefore;
    private final Map<String, String> fileContentsCurrent;
    private final ASTDiff astDiff;
    private final HumanReadableDiff result;

    public HumanReadableDiff getResult() {
        return result;
    }

    public HumanReadableDiffGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, CaseInfo info, Configuration configuration) {
        this.repo = info.getRepo();
        this.commit = info.getCommit();
        this.fileContentsBefore = projectASTDiff.getFileContentsBefore();
        this.fileContentsCurrent = projectASTDiff.getFileContentsAfter();
        this.astDiff = generated;
        result = new HumanReadableDiff();
        make();
    }

    public ASTDiff getAstDiff() {
        return astDiff;
    }
    abstract void make();

    public static void addAccordingly(AbstractMapping abstractMapping, NecessaryMappings target) {
        if (!abstractMapping.getLeftType().equals(abstractMapping.getRightType()))
            return;
        if (isProgramElement(abstractMapping.getLeftType()))
            target.getMatchedElements().add(abstractMapping);
        else
            target.getMappings().add(abstractMapping);
    }

    static boolean isProgramElement(String leftType) {
        return leftType.equals(Constants.TYPE_DECLARATION) ||
                leftType.equals(Constants.METHOD_DECLARATION) ||
                leftType.equals(Constants.FIELD_DECLARATION) ||
                leftType.equals(Constants.ENUM_DECLARATION);
    }
    void handleFieldDeclaration(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        //TODO: MUST BE IMPLEMENTED ASAP
    }
    void handleMethodDeclaration(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        for (Mapping signatureMapping : getMethodSignatureMappings(mapping, astDiff.getAllMappings())) {
            addAccordingly(getAbstractMapping(signatureMapping, srcPath, dstPath), target);
        }
    }
    void handleTypeDeclaration(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        Set<Mapping> classSignatureMappings = getClassSignatureMappings(mapping, astDiff.getAllMappings());
        for (Mapping signatureMapping : classSignatureMappings) {
            addAccordingly(getAbstractMapping(signatureMapping, srcPath, dstPath), target);
        }
    }
    void handleStatement(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
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
            else if (_met && !child.getType().name.equals(Constants.SIMPLE_TYPE))
                break;
            Set<Tree> dsts = mappings.getDsts(child);
            if (dsts == null) continue;
            for (Tree mappedDst : dsts) {
                if (sameHierarchy(mappedDst,mapping.second))
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
                if (sameHierarchy(mappedDst,mapping.second))
                    abstractMappingSet.add(new Mapping(child, mappedDst));
            }
        }
        return abstractMappingSet;
    }
    public static boolean sameHierarchy(Tree node, Tree ancestor) {
        if (node == ancestor) {
            return true;
        }
        if (node.getParent() == null) return false;
        return sameHierarchy(node.getParent(), ancestor);
    }
    public AbstractMapping getAbstractMapping(Mapping mapping, String srcPath, String dstPath) {
        String srcContent = getSrcContent(srcPath);
        String dstContent = getDstContent(dstPath);
        String srcString = "",dstString = "";
        srcString = getString(mapping.first, srcContent);
        dstString = getString(mapping.second, dstContent);
        return new AbstractMapping(mapping, srcString, dstString);
    }
    public static AbstractMapping getAbstractMappingFromContent(Mapping mapping, String srcContent, String dstContent) {
        String srcString = "",dstString = "";
        srcString = getString(mapping.first, srcContent);
        dstString = getString(mapping.second, dstContent);
        return new AbstractMapping(mapping, srcString, dstString);
    }

    String getDstContent(String dstPath) {
        return fileContentsCurrent.get(dstPath);
    }

    String getSrcContent(String srcPath) {
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
        return content.substring(start, end);
    }
    public void write(String output_folder, String srcPath, String fileName) {
        File file = new File(PathResolver.getCommonPath(output_folder, srcPath, fileName, commit, repo));
        HumanReadableDiff.write(file, result);
    }
}


