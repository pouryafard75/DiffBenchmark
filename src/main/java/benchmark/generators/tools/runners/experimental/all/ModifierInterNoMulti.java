package benchmark.generators.tools.runners.experimental.all;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.generators.tools.runners.experimental.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.experimental.labels.TreeModifier;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.safeAdd;

/* Created by pourya on 2024-05-23*/
public class ModifierInterNoMulti extends PipelinedASTDiffProvider {

    public ModifierInterNoMulti(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase caseInfo, Matcher matcher) {
        super(treeModifier, projectMatcher, null, projectASTDiff, input, caseInfo, matcher);
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
