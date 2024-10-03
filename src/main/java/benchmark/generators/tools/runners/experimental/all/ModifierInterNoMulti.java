package benchmark.generators.tools.runners.experimental.all;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.runners.experimental.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.experimental.labels.TreeModifier;
import benchmark.utils.Experiments.IQuerySelector;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.safeAdd;

/* Created by pourya on 2024-05-23*/
public class ModifierInterNoMulti extends PipelinedASTDiffProvider {

    public ModifierInterNoMulti(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, IBenchmarkCase benchmarkCase, IQuerySelector query, Matcher matcher) {
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
