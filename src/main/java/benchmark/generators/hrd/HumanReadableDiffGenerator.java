package benchmark.generators.hrd;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.filters.NoFilter;
import benchmark.models.hrd.AbstractMapping;
import benchmark.models.hrd.HumanReadableDiff;
import benchmark.models.hrd.NecessaryMappings;
import benchmark.utils.PathResolver;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.TreeAddition;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.model.MoveIn;
import org.refactoringminer.astDiff.actions.model.MoveOut;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.Constants;

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
    private final HumanReadableDiffFilter generationFilter;

    private HumanReadableDiff result;

    public HumanReadableDiffGenerator(IBenchmarkCase benchmarkCase, ASTDiff current) {
        this(benchmarkCase, current, new NoFilter());
    }
    public HumanReadableDiffGenerator(IBenchmarkCase benchmarkCase, ASTDiff current, HumanReadableDiffFilter filter) {
        //TODO
        this.repo = benchmarkCase.getRepo();
        this.commit = benchmarkCase.getCommit();
        ProjectASTDiff projectASTDiff = benchmarkCase.getProjectASTDiff();
        this.fileContentsBefore = projectASTDiff.getFileContentsBefore();
        this.fileContentsCurrent = projectASTDiff.getFileContentsAfter();
        this.astDiff = current;
        this.generationFilter = filter;
        result = new HumanReadableDiff();
        make();
    }

    public HumanReadableDiff getResult() {
        return result;
    }


    private void make(){
        List<Mapping> mappings = new ArrayList<>(getAstDiff().getAllMappings().getMappings());
        mappings.sort(Comparator.comparingInt(o -> o.first.getPos()));
        for (Mapping mapping : mappings ) {
            if (isPartOf(mapping, Constants.JAVA_DOC)) continue; //TODO: It might be extremely useful to have this information in the future
            if (isBetweenDifferentTypes(mapping)) {
//                throw new RuntimeException();
//                System.out.println("");
            }
            MappingMetaInformation mappingMetaInformation = getMetaInformation(mapping);
            if (mappingMetaInformation == null) continue;
            makeForEachMapping(mappingMetaInformation);
        }
        extractMultiCollection(); //TODO: the result of this call?
        result = generationFilter.make(result, result);
    }

    private Set<Mapping> extractMultiCollection() {
        Set<Mapping> multiCollection = new HashSet<>();
        Map<Tree, Set<Tree>> treeSetMap = getAstDiff().getAllMappings().srcToDstMultis();

        for (Map.Entry<Tree, Set<Tree>> entry : treeSetMap.entrySet()) {
            Tree t1 = entry.getKey();
            Set<Tree> value = entry.getValue();
            for (Tree t2 : value) {
                multiCollection.add(new Mapping(t1, t2));
            }
        }
        return multiCollection;
    }

    public abstract void handleMethodDeclaration(MappingMetaInformation mappingMetaInformation);

    private MappingMetaInformation getMetaInformation(Mapping mapping) {
        if (isInterFileMapping(mapping, getAstDiff().src.getRoot(), getAstDiff().dst.getRoot()))
            return getMappingMetaInformationForInterFileMappings(mapping);
        return new MappingMetaInformation(getAstDiff().getSrcPath(), getAstDiff().getDstPath(), getResult().getIntraFileMappings(), mapping, false);
    }

    private MappingMetaInformation getMappingMetaInformationForInterFileMappings(Mapping mapping) {
        if (isProgramElement(mapping.first.getType().name)|| isStatement(mapping.first.getType().name)) {
            EditScript editScript = getAstDiff().editScript;
            for (Action e : editScript) {
                NecessaryMappings target;
                String currSrc;
                String currDst;
                if (e instanceof MoveOut && actionBelongsToMapping(mapping, e)) {
                    String key = e.toString();
                    key = key.replace("moved to file", "Moved to File");
                    getResult().getInterFileMappings().putIfAbsent(key, new NecessaryMappings());
                    target = getResult().getInterFileMappings().get(key);
                    currSrc = getAstDiff().getSrcPath();
                    currDst = ((MoveOut) e).getDstFile();
                    return new MappingMetaInformation(currSrc, currDst, target, mapping, true);
                }
                else if (e instanceof MoveIn && actionBelongsToMapping(mapping, e))
                {
                    String key = e.toString();
                    key = key.replace("moved from file", "Moved from File");
                    getResult().getInterFileMappings().putIfAbsent(key, new NecessaryMappings());
                    target = getResult().getInterFileMappings().get(key);
                    currSrc = ((MoveIn) e).getSrcFile();
                    currDst = getAstDiff().getDstPath();
                    return new MappingMetaInformation(currSrc, currDst, target, mapping, true);
                }
            }
        }
        return null;
    }

    private static boolean actionBelongsToMapping(Mapping mapping, Action e) {
        return e.getNode().toTreeString().equals(mapping.first.toTreeString()) ||
                e.getNode().toTreeString().equals(mapping.second.toTreeString()) ||
                ((TreeAddition) e).getParent().toTreeString().equals(mapping.first.toTreeString()) ||
                ((TreeAddition) e).getParent().toTreeString().equals(mapping.second.toTreeString());
    }

    public ASTDiff getAstDiff() {
        return astDiff;
    }
    public void makeForEachMapping(MappingMetaInformation mappingMetaInformation) {
        if (isProgramElement(mappingMetaInformation.mapping.first.getType().name)){
            handleProgramElement(mappingMetaInformation);
        }
        handleNonProgramElements(mappingMetaInformation);
    }

    public void handleProgramElement(MappingMetaInformation mappingMetaInformation) {
        switch (mappingMetaInformation.mapping.first.getType().name)
        {
            case Constants.TYPE_DECLARATION:
                handleTypeDeclaration(mappingMetaInformation);
                break;
            case Constants.METHOD_DECLARATION:
                handleMethodDeclaration(mappingMetaInformation);
                break;
            case Constants.FIELD_DECLARATION:
                handleFieldDeclaration(mappingMetaInformation);
                break;
            case Constants.ENUM_DECLARATION:
                handleEnumDeclaration(mappingMetaInformation);
                break;
        }
    }

    public abstract void handleNonProgramElements(MappingMetaInformation mappingMetaInformation);
    public abstract void handleEnumDeclaration(MappingMetaInformation mappingMetaInformation);
    public abstract void handleFieldDeclaration(MappingMetaInformation mappingMetaInformation);
    public abstract void handleTypeDeclaration(MappingMetaInformation mappingMetaInformation);

    void handleStatement(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        if (isStatement(mapping.first.getType().name))
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
    }

    public static class MappingMetaInformation {
        public final String currSrc;
        public final String currDst;
        public final NecessaryMappings target;
        public final Mapping mapping;
        public final boolean _inter;

        public MappingMetaInformation(String currSrc, String currDst, NecessaryMappings target, Mapping mapping, boolean _inter) {
            this.currSrc = currSrc;
            this.currDst = currDst;
            this.target = target;
            this.mapping = mapping;
            this._inter = _inter;
        }
    }

    public void addAccordingly(AbstractMapping abstractMapping, NecessaryMappings target) {
//        if (abstractMapping.getLeftType().equals(Constants.COMPILATION_UNIT)) return;
        if (isProgramElement(abstractMapping.getLeftType()))
            target.getMatchedElements().add(abstractMapping);
        else
            target.getMappings().add(abstractMapping);
    }
    public static boolean isProgramElement(String leftType) {
        return leftType.equals(Constants.TYPE_DECLARATION) ||
                leftType.equals(Constants.METHOD_DECLARATION) ||
                leftType.equals(Constants.FIELD_DECLARATION) ||
                leftType.equals(Constants.ENUM_DECLARATION);
    }
    public static boolean isBetweenDifferentTypes(Mapping mapping) {
        return !mapping.first.getType().name.equals(mapping.second.getType().name);
    }
    public static boolean isPartOf(Mapping mapping, String type) {
        if (isPartOf(mapping.first, type) || isPartOf(mapping.second, type))
            return true;
        return false;
    }
    public static boolean isPartOf(Tree srcSubTree, String type) {
        if (srcSubTree.getType().name.equals(type))
            return true;
        if (srcSubTree.getParent() == null) return false;
        return isPartOf(srcSubTree.getParent(), type);
    }
    public static boolean isInterFileMapping(Mapping mapping, Tree src, Tree dst) {
        if (mapping.first.getParent() != null & mapping.second.getParents() != null)
        {
            Tree srcLast = getParentUntilType(mapping.first,Constants.COMPILATION_UNIT);
            Tree dstLast = getParentUntilType(mapping.second,Constants.COMPILATION_UNIT);
            if (srcLast == null || dstLast == null)
                throw new RuntimeException("Something unexpected");
            if (!srcLast.equals(src) || !dstLast.equals(dst))
                return true;
        }
        return false;
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
    String generateEnumSignature(Tree tree, String content) {
        String name = "";
        for (Tree child : tree.getChildren()) {
            if (child.getType().name.equals(Constants.SIMPLE_NAME))
            {
                name = child.getLabel();
            }
        }
        if (name.equals(""))
            throw new RuntimeException("Enum name not found");
        else
            return "enum : " + name;
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


