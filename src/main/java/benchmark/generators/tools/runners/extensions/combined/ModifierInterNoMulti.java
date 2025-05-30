package benchmark.generators.tools.runners.extensions.combined;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.runners.extensions.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.extensions.labels.TreeModifier;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.safeAdd;

/* Created by pourya on 2024-05-23*/
public class ModifierInterNoMulti extends PipelinedASTDiffProvider {

    public ModifierInterNoMulti(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, IBenchmarkCase benchmarkCase, DiffSelector query, Matcher matcher) {
        super(treeModifier, projectMatcher, null,
                benchmarkCase, query, matcher);
    }

    String getPipelineName() {
        return "MI"; //ModifierInter
    }

    @Override
    ExtendedMultiMappingStore matchTreeCopies(Tree srcCopy, Tree dstCopy) {
        MappingStore mappingStore = modifyTreeAndApplyProjectMatcher(srcCopy, dstCopy);
        ExtendedMultiMappingStore result = new ExtendedMultiMappingStore(srcCopy, dstCopy);
        safeAdd(result, mappingStore);
        return result;
    }
}
