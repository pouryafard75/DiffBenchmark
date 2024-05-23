package benchmark.generators.tools.runners.hacks.multimapping;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeUtils;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.match;
import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.safeAdd;

/* Created by pourya on 2024-04-30*/
public class NonMatchedSubtreesAdditionalRound implements GumTreeMultiMappingMatcher {

    private static final int minHeight = new GreedySubtreeMatcher().getMinPriority();

    private static void addCopyPastes(Tree srcRoot, Tree dstRoot, ExtendedMultiMappingStore extendedMultiMappingStore, Matcher matcher) {
        for (Tree subTree : TreeUtils.breadthFirst(srcRoot)) {
            if (subTree.getMetrics().height > minHeight && !extendedMultiMappingStore.isSrcMappedConsideringSubTrees(subTree)) {
                MappingStore match = match(subTree, dstRoot, matcher);
                safeAdd(extendedMultiMappingStore, match);
            }
        }
        for (Tree subTree : TreeUtils.breadthFirst(dstRoot)) {
            if (subTree.getMetrics().height > 1 && !extendedMultiMappingStore.isDstMappedConsideringSubTrees(subTree)) {
                MappingStore match = match(srcRoot, subTree, matcher);
                safeAdd(extendedMultiMappingStore, match);
            }
        }
    }

    @Override
    public ExtendedMultiMappingStore multimatch(Tree srcRoot, Tree dstRoot, Matcher matcher, MappingStore mappings) {
        ExtendedMultiMappingStore extendedMultiMappingStore = new ExtendedMultiMappingStore(srcRoot, dstRoot);
        safeAdd(extendedMultiMappingStore, mappings);
        addCopyPastes(srcRoot, dstRoot, extendedMultiMappingStore, matcher);
        return extendedMultiMappingStore;
    }
}
