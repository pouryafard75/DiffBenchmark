package benchmark.oracle.generators.tools.runners;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.*;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/* Created by pourya on 2024-04-27*/
public class CommitLevelGumTreeASTDiff extends ASTDiffConvertor{

    protected final Matcher matcher;
    protected final CaseInfo info;
    protected final Configuration configuration;

    public CommitLevelGumTreeASTDiff(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(rm_astDiff.getSrcPath(), projectASTDiff);
        this.info = caseInfo;
        this.configuration = configuration;
        this.matcher = matcher;
    }

    @Override
    protected List<MappingExportModel> getExportedMappings() {
        MappingStore match = getCommitLevelFullMatch();
        Collection<Mapping> mappings = new LinkedHashSet<>();
        for (Mapping mapping : match) {
            if (belongsTo(mapping.first, ptc.get(rm_astDiff.getSrcPath()).getRoot()) || belongsTo(mapping.second, ctc.get(rm_astDiff.getDstPath()).getRoot()))
                mappings.add(mapping);
        }
        return MappingExportModel.exportModelList(mappings);
    }


    public MappingStore getCommitLevelFullMatch() {
        return new CommitLevelMatcher(matcher, ptc.values(), ctc.values()).makeFullCommitMatch();
    }

    private boolean belongsTo(Tree subtree, Tree root) {
        if (subtree == root) return true;
        Tree parent = subtree.getParent();
        while (parent != null) {
            if (parent == root) return true;
            parent = parent.getParent();
        }
        return false;
    }

    static class CommitLevelMatcher {
        private final Matcher matcher;
        private final Collection<TreeContext> ptc;
        private final Collection<TreeContext> ctc;

        CommitLevelMatcher(Matcher matcher, Collection<TreeContext> ptc, Collection<TreeContext> ctc) {
            this.matcher = matcher;
            this.ptc = ptc;
            this.ctc = ctc;
        }
        private MappingStore makeFullCommitMatch() {
            Tree srcVN = makeVirtualNode(ptc);
            Tree dstVN = makeVirtualNode(ctc);
            return matcher.match(srcVN, dstVN);
        }

        private Tree makeVirtualNode(Collection<TreeContext> treeContextCollection) {
            FakeTree root = new FakeTree();
            for (TreeContext treeContext : treeContextCollection) {
                root.addChild(treeContext.getRoot());
            }
            return root;
        }
    }
}
