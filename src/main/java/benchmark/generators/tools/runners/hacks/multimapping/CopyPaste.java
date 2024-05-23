package benchmark.generators.tools.runners.hacks.multimapping;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.MultiMappingStore;
import com.github.gumtreediff.matchers.heuristic.gt.DefaultPriorityTreeQueue;
import com.github.gumtreediff.matchers.heuristic.gt.PriorityTreeQueue;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import java.util.Set;
import java.util.function.Function;

import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.match;
import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.safeAdd;

/* Created by pourya on 2024-05-12*/
public class CopyPaste implements GumTreeMultiMappingMatcher{

    @Override
    public ExtendedMultiMappingStore multimatch(Tree srcRoot, Tree dstRoot, Matcher matcher, MappingStore mappings) {
        ExtendedMultiMappingStore extendedMultiMappingStore = new ExtendedMultiMappingStore(srcRoot, dstRoot);
        safeAdd(extendedMultiMappingStore, mappings);
        MultiMappingStore candidates = new CopyPasteCandidateFinder().find(srcRoot, dstRoot);
        for (Tree dstSubTree : dstRoot.breadthFirst()) {
            if (mappings.isDstMapped(dstSubTree)) continue;
            if (candidates.hasDst(dstSubTree)) {
                Set<Tree> srcs = candidates.getSrcs(dstSubTree);
                for (Tree srcSubTree : srcs) {
                    if (srcSubTree.isIsoStructuralTo(dstSubTree))
                    {
                        MappingStore temp = new MappingStore(srcRoot, dstRoot);
                        temp.addMappingRecursively(srcSubTree, dstSubTree);
                        safeAdd(extendedMultiMappingStore, temp);
                    }

                }
            }
        }
        return extendedMultiMappingStore;
    }

    private static class CopyPasteCandidateFinder{
        private static final int DEFAULT_MIN_PRIORITY = 1;
        private static final String DEFAULT_PRIORITY_CALCULATOR = "height";
        private static final Function<Tree, Integer> priorityCalculator = PriorityTreeQueue.getPriorityCalculator(DEFAULT_PRIORITY_CALCULATOR);
        public boolean isAnySimilarLeaf(Tree src, Tree dst){
            if (src.isLeaf() && dst.isLeaf())
                return src.getLabel().equals(dst.getLabel());
            for (int i = 0; i < src.getChildren().size(); i++)
                if (isAnySimilarLeaf(src.getChild(i), dst.getChild(i)))
                    return true;
            return false;
        }
        public MultiMappingStore find(Tree src, Tree dst) {
            var multiMappings = new MultiMappingStore();
            PriorityTreeQueue srcTrees = new DefaultPriorityTreeQueue(src, DEFAULT_MIN_PRIORITY, priorityCalculator);
            PriorityTreeQueue dstTrees = new DefaultPriorityTreeQueue(dst, DEFAULT_MIN_PRIORITY, priorityCalculator);

            while (!(srcTrees.isEmpty() || dstTrees.isEmpty())) {
                PriorityTreeQueue.synchronize(srcTrees, dstTrees);
                if (srcTrees.isEmpty() || dstTrees.isEmpty())
                    break;

                var currentPrioritySrcTrees = srcTrees.pop();
                var currentPriorityDstTrees = dstTrees.pop();

                for (var currentSrc : currentPrioritySrcTrees)
                    for (var currentDst : currentPriorityDstTrees)
                        if (currentSrc.getMetrics().structureHash == currentDst.getMetrics().structureHash)
                            if (currentSrc.isIsoStructuralTo(currentDst))
                                if (isAnySimilarLeaf(currentSrc, currentDst))
                                    multiMappings.addMapping(currentSrc, currentDst);

                for (var t : currentPrioritySrcTrees)
                    if (!multiMappings.hasSrc(t))
                        srcTrees.open(t);
                for (var t : currentPriorityDstTrees)
                    if (!multiMappings.hasDst(t))
                        dstTrees.open(t);
            }
            return multiMappings;
        }

    }

}
