package benchmark.generators.tools.runners.hacks.all;

import benchmark.generators.tools.runners.hacks.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.hacks.labels.TreeModifier;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import static benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider.safeAdd;

/* Created by pourya on 2024-05-23*/
public class ModifierInterNoMulti extends PipelinedASTDiffProvider {

    public ModifierInterNoMulti(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(treeModifier, projectMatcher, null, projectASTDiff, input, caseInfo, configuration, matcher);
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
