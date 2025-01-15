package benchmark.generators.tools.runners.experimental.all;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.runners.experimental.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.experimental.labels.TreeModifier;
import benchmark.generators.tools.runners.experimental.multimapping.GumTreeMultiMappingMatcher;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-05-10*/

//XXX: This must be verified, I haven't tested this one tbh...
public class ModifierInterMulti extends PipelinedASTDiffProvider {
    public ModifierInterMulti(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, GumTreeMultiMappingMatcher multiMappingMatcher, IBenchmarkCase benchmarkCase, DiffSelector querySelector, Matcher matcher) {
        super(treeModifier, projectMatcher, multiMappingMatcher,
                benchmarkCase, querySelector, matcher);
    }

    @Override
    String getPipelineName() {
        return "MIM"; //ModifierInterMulti
    }

    @Override
    ExtendedMultiMappingStore matchTreeCopies(Tree srcCopy, Tree dstCopy) {
        MappingStore mappingStore = modifyTreeAndApplyProjectMatcher(srcCopy, dstCopy);

        //MultiMappings
        ExtendedMultiMappingStore multimatch = multiMappingMatcher.multimatch(srcCopy, dstCopy, matcher, mappingStore);

        return multimatch;
    }


}
