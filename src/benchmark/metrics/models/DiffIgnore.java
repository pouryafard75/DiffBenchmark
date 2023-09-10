package benchmark.metrics.models;

import benchmark.oracle.models.AbstractMapping;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static benchmark.oracle.generators.HumanReadableDiffGenerator.*;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.isStatement;

/* Created by pourya on 2023-04-16 5:07 a.m. */
public class DiffIgnore {
    private final Tree src;
    private final Tree dst;
    private final MappingStore mappings;
//    private final HumanReadableDiff computed = new HumanReadableDiff();
    private final Set<AbstractMapping> ignoredMappingSet = new HashSet<>();
    private int _ignoredElements;
    private int _ignoredMappings;

    public int getNumberOfIgnoredMappings() {
        return this._ignoredMappings;
    }
    public int getNumberOfIgnoredElements() {
        return this._ignoredElements;
    }

    public DiffIgnore(Tree src, Tree dst) {
        this.src = src;
        this.dst = dst;
        mappings = new GreedySubtreeMatcher() {
            @Override
            protected void retainBestMapping(List<Mapping> mappingList, Set<Tree> srcIgnored, Set<Tree> dstIgnored) {
                //Do nothing in case of multiple candidates
            }
        }.match(src, dst);
        this.make();
    }


    private void make(){
        this.run();
    }
    private void run(){
        ExtendedMultiMappingStore extendedMultiMappingStore = makeExtendedMultiMappingStore(src,dst,mappings);
        for (Mapping mapping : mappings) {
            if (isInterFileMapping(mapping,src,dst)) continue;
            if (isPartOfJavadoc(mapping)) continue;
            if (isBetweenDifferentTypes(mapping)) continue;
            if (!mapping.first.isIsomorphicTo(mapping.second)) continue;
            if (!fromSameEnclosingClass(mapping)) continue;
            //Second type definitely has the same type due to the previous check
            switch (mapping.first.getType().name) //Second type definitely has the same type due to the previous check
            {
                case Constants.TYPE_DECLARATION:
                    _ignoredMappings += getClassSignatureMappings(mapping,extendedMultiMappingStore).size();
                    _ignoredElements += 1;
                    break;
                case Constants.METHOD_DECLARATION:
                    _ignoredMappings += getMethodSignatureMappings(mapping,extendedMultiMappingStore).size();
                    _ignoredElements += 1;
                    break;
                case Constants.FIELD_DECLARATION:
//                    _ignoredMappings += getFieldSignatureMappings(mapping,mappings).size();
                    _ignoredElements += 1;
                    break;
            }
            if (isStatement(mapping.first.getType().name)) {
                Tree srcEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.first.getParent(), Constants.METHOD_DECLARATION);
                Tree dstEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.second.getParent(), Constants.METHOD_DECLARATION);
                if (srcEnclosingMethod != null && dstEnclosingMethod != null)
                    if (srcEnclosingMethod.isIsomorphicTo(dstEnclosingMethod)) {
                        _ignoredMappings += 1;
                    }
            }
        }
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
