package benchmark.generators.tools.runners.gt.hacks.interfile;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

/* Created by pourya on 2024-04-30*/
public class StagedTreeMatchingGumTreeASTDiffProvider extends AbstractProjectGumTreeASTDiffProvider {
    public StagedTreeMatchingGumTreeASTDiffProvider(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(projectASTDiff, rm_astDiff, caseInfo, configuration, matcher);
    }

    public MappingStore getCommitLevelFullMatch() {
        Tree srcVN = makeVirtualNode(ptc.values());
        Tree dstVN = makeVirtualNode(ctc.values());
        MappingStore match = new MappingStore(srcVN, dstVN);
        for (ASTDiff astDiff : this.projectASTDiff.getDiffSet()) {
            Tree src = ptc.get(astDiff.getSrcPath()).getRoot();
            Tree dst = ctc.get(astDiff.getDstPath()).getRoot();
            Tree srcParent = src.getParent();
            Tree dstParent = dst.getParent();
            src.setParent(null);
            dst.setParent(null);
            match = matcher.match(src, dst, match);
            src.setParent(srcParent);
            dst.setParent(dstParent);
        }
        return matcher.match(srcVN, dstVN, match);
    }
}
