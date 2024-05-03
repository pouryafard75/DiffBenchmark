package benchmark.generators.tools.runners.gt.hacks.interfile;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

/* Created by pourya on 2024-04-27*/
public class SingleVirtualNodeGumTreeASTDiffProvider extends AbstractProjectGumTreeASTDiffProvider {

    public SingleVirtualNodeGumTreeASTDiffProvider(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(projectASTDiff, rm_astDiff, caseInfo, configuration, matcher);
    }

    public MappingStore getCommitLevelFullMatch() {
        return makeFullCommitMatch();
    }

    private MappingStore makeFullCommitMatch() {
        Tree srcVN = makeVirtualNode(ptc.values());
        Tree dstVN = makeVirtualNode(ctc.values());
        return matcher.match(srcVN, dstVN);
    }
}
