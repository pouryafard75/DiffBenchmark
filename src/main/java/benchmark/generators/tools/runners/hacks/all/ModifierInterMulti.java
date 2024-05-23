package benchmark.generators.tools.runners.hacks.all;

import benchmark.generators.tools.runners.hacks.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.hacks.labels.TreeModifier;
import benchmark.generators.tools.runners.hacks.multimapping.GumTreeMultiMappingMatcher;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-05-10*/
public class ModifierInterMulti extends PipelinedASTDiffProvider {
    public ModifierInterMulti(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, GumTreeMultiMappingMatcher multiMappingMatcher, ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(treeModifier, projectMatcher, multiMappingMatcher, projectASTDiff, input, caseInfo, configuration, matcher);
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
