package benchmark.metrics.models;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.MultiMappingStore;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static benchmark.oracle.generators.HumanReadableDiffGenerator.*;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.isStatement;

/* Created by pourya on 2023-04-16 5:07 a.m. */
public class TrivialDiff {
    private final ASTDiff rm_astDiff;
    private final HumanReadableDiff humanReadableDiff = new HumanReadableDiff();
    private final NecessaryMappings target = humanReadableDiff.intraFileMappings;

    public ASTDiff makeASTDiff() {
        MappingStore mappings = new GreedySubtreeMatcher() {
            @Override
            public void filterMappings(MultiMappingStore multiMappings) {
                Set<Tree> ignored = new HashSet<>();
                for (var src : multiMappings.allMappedSrcs()) {
                    var isMappingUnique = false;
                    if (multiMappings.isSrcUnique(src)) {
                        var dst = multiMappings.getDsts(src).stream().findAny().get();
                        if (multiMappings.isDstUnique(dst)) {
                            if (isAcceptable(src, dst))
                                mappings.addMappingRecursively(src, dst);
                        }
                    }
                }
            }

            @Override
            protected void retainBestMapping(List<Mapping> mappingList, Set<Tree> srcIgnored, Set<Tree> dstIgnored) {
                //Do nothing in case of multiple candidates
            }
        }.match(rm_astDiff.src.getRoot(),rm_astDiff.dst.getRoot());
        ExtendedMultiMappingStore extendedMultiMappingStore = new ExtendedMultiMappingStore(rm_astDiff.src.getRoot(), rm_astDiff.dst.getRoot());
        extendedMultiMappingStore.add(mappings);
        return new ASTDiff(rm_astDiff.getSrcPath(),rm_astDiff.getDstPath(),rm_astDiff.src,rm_astDiff.dst, extendedMultiMappingStore);
    }
    static class DiffIgnoreStats{
        int _ignoredMappings;
        int _ignoredElements;
        public int get_ignoredMappings() {
            return _ignoredMappings;
        }
        public int get_ignoredElements() {
            return _ignoredElements;
        }
        public DiffIgnoreStats(int _ignoredMappings, int _ignoredElements) {
            this._ignoredMappings = _ignoredMappings;
            this._ignoredElements = _ignoredElements;
        }
    }

    public HumanReadableDiff getHumanReadableDiff() {
        return humanReadableDiff;
    }

    public TrivialDiff(ASTDiff rm_astDiff){
        this.rm_astDiff = rm_astDiff;
    }

    private boolean isAcceptable(Tree src, Tree dst) {
        if (!src.getType().name.equals(dst.getType().name)) throw new RuntimeException("Types are not equal");
        switch (src.getType().name) {
            case Constants.TYPE_DECLARATION:
            case Constants.METHOD_DECLARATION:
            case Constants.FIELD_DECLARATION:
            case Constants.ENUM_DECLARATION:
                return true;
            default:
                return false;
        }
    }

//    private DiffIgnoreStats calculate(HumanReadableDiff god){
//        int ignoredMappings = calcIntersectionSize(god.intraFileMappings.getMappings(), humanReadableDiff.intraFileMappings.getMappings());
//        int ignoredElements = calcIntersectionSize(god.intraFileMappings.getMatchedElements(), humanReadableDiff.intraFileMappings.getMatchedElements());
//        return new DiffIgnoreStats(ignoredMappings, ignoredElements);
//    }

//    private int calcIntersectionSize(Set<AbstractMapping> set1, Set<AbstractMapping> set2) {
//        int ret = 0;
//        for (AbstractMapping abstractMapping : set1) {
//            for (AbstractMapping mapping : set2) {
//                if (abstractMapping.equals(mapping))
//                {
//                    ret += 1;
//                    break;
//                }
//            }
//        }
//        return ret;
//    }
    private void run(){
//        ExtendedMultiMappingStore extendedMultiMappingStore = makeExtendedMultiMappingStore(src,dst,mappings);
//        for (Mapping mapping : mappings) {
//            if (isInterFileMapping(mapping,src,dst)) continue;
//            if (isPartOfJavadoc(mapping)) continue;
//            if (isBetweenDifferentTypes(mapping)) continue;
//            if (!mapping.first.isIsomorphicTo(mapping.second)) continue;
//            if (!fromSameEnclosingClass(mapping)) continue;
//            //Second type definitely has the same type due to the previous check
//            switch (mapping.first.getType().name) //Second type definitely has the same type due to the previous check
//            {
////                case Constants.TYPE_DECLARATION:
////                    addAccordingly(
////                            new AbstractMapping(mapping,
////                                    generateClassSignature(mapping.first,srcContent),
////                                    generateClassSignature(mapping.second,dstContent)),
////                            target);
////
////                    Set<Mapping> classSignatureMappings = getClassSignatureMappings(mapping, extendedMultiMappingStore);
////                    for (Mapping classSignatureMapping : classSignatureMappings)
////                        addAccordingly(getAbstractMappingFromContent(classSignatureMapping, srcContent, dstContent), target);
////                    _ignoredMappings += classSignatureMappings.size();
////                    _ignoredElements += 1;
////                    break;
////                case Constants.METHOD_DECLARATION:
////                    addAccordingly(
////                            new AbstractMapping(mapping,
////                                    generateMethodSignature(mapping.first,srcContent),
////                                    generateMethodSignature(mapping.second,dstContent)),
////                            target);
////                    Set<Mapping> methodSignatureMappings = getMethodSignatureMappings(mapping, extendedMultiMappingStore);
////                    for (Mapping classSignatureMapping : methodSignatureMappings)
////                        addAccordingly(getAbstractMappingFromContent(classSignatureMapping, srcContent, dstContent), target);
////
////                    _ignoredMappings += methodSignatureMappings.size();
////                    _ignoredElements += 1;
//////                    System.out.println("from method" + _ignoredElements);
////                    break;
////                case Constants.FIELD_DECLARATION:
////                    addAccordingly(
////                            new AbstractMapping(mapping,
////                                    generateFieldSignature(mapping.first,srcContent),
////                                    generateFieldSignature(mapping.second,dstContent)),
////                            target);
////                    Set<Mapping> generateFieldSignature = getMethodSignatureMappings(mapping, extendedMultiMappingStore);
////
////
//////                    _ignoredMappings += getFieldSignatureMappings(mapping,mappings).size();
////                    _ignoredElements += 1;
////                    break;
//            }
//            if (isStatement(mapping.first.getType().name)) {
////                Tree srcEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.first.getParent(), Constants.METHOD_DECLARATION);
////                Tree dstEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.second.getParent(), Constants.METHOD_DECLARATION);
////                if (srcEnclosingMethod != null && dstEnclosingMethod != null)
////                    if (srcEnclosingMethod.isIsomorphicTo(dstEnclosingMethod)) {
////                        _ignoredMappings += 1;
////                        addAccordingly(getAbstractMappingFromContent(mapping, srcContent, dstContent), humanReadableDiff.intraFileMappings);
////                    }
//            }
//        }
    }

    private ExtendedMultiMappingStore makeExtendedMultiMappingStore(Tree src, Tree dst, MappingStore mappings) {
        ExtendedMultiMappingStore ret = new ExtendedMultiMappingStore(src, dst);
        ret.add(mappings);
        return ret;
    }

    private static boolean fromSameEnclosingClass(Mapping mapping) {
        Tree srcEnclosingClass = TreeUtilFunctions.getParentUntilType(mapping.first, Constants.TYPE_DECLARATION);
        Tree dstEnclosingClass = TreeUtilFunctions.getParentUntilType(mapping.second, Constants.TYPE_DECLARATION);
        //TODO MAKE IT WORK FOR IMPORTS AND PACKAGES!!
        if (srcEnclosingClass == null || dstEnclosingClass == null) return false;
        Tree srcClassSimpleName = firstChildOfType(srcEnclosingClass,Constants.SIMPLE_NAME);
        Tree dstClassSimpleName = firstChildOfType(dstEnclosingClass,Constants.SIMPLE_NAME);
        if (srcClassSimpleName == null || dstClassSimpleName == null) return false;
        return srcClassSimpleName.getLabel().equals(dstClassSimpleName.getLabel());
    }

    private static Tree firstChildOfType(Tree srcEnclosingClass, String inputType) {
        for (Tree child : srcEnclosingClass.getChildren()) {
            if (child.getType().name.equals(inputType))
            {
                return child;
            }
        }
        return null;
    }
}
