package benchmark.oracle.generators.tools.runners.gt.hacks.interfile;

import benchmark.oracle.generators.tools.runners.converter.AbstractASTDiffProviderFromExportedMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.FakeTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

/* Created by pourya on 2024-04-30*/
public abstract class AbstractProjectGumTreeASTDiffProvider extends AbstractASTDiffProviderFromExportedMappings {
    protected final Matcher matcher;
    protected AbstractProjectGumTreeASTDiffProvider(ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(projectASTDiff, input, caseInfo, configuration);
        this.matcher = matcher;
    }

    public AbstractProjectGumTreeASTDiffProvider(AbstractProjectGumTreeASTDiffProvider projectGumTreeASTDiff) {
        super(projectGumTreeASTDiff.projectASTDiff, projectGumTreeASTDiff.input, projectGumTreeASTDiff.info, projectGumTreeASTDiff.conf);
        this.matcher = projectGumTreeASTDiff.matcher;
    }

    boolean belongsTo(Tree subtree, Tree root) {
        if (subtree == root) return true;
        Tree parent = subtree.getParent();
        while (parent != null) {
            if (parent == root) return true;
            parent = parent.getParent();
        }
        return false;
    }

    @Override
    protected List<MappingExportModel> getExportedMappings() {
        MappingStore match = getCommitLevelFullMatch();
        Collection<Mapping> mappings = new LinkedHashSet<>();
        for (Mapping mapping : match) {
            if (belongsTo(mapping.first, ptc.get(input.getSrcPath()).getRoot())
                    ||
                belongsTo(mapping.second, ctc.get(input.getDstPath()).getRoot()))
                    mappings.add(mapping);
        }
        return MappingExportModel.exportModelList(mappings);
    }

    Tree makeVirtualNode(Collection<TreeContext> treeContextCollection) {
        FakeTree root = new FakeTree();
        for (TreeContext treeContext : treeContextCollection) {
            Tree thisRoot = treeContext.getRoot();
            thisRoot.setParent(null);
            root.addChild(thisRoot);
        }
        return root;
    }
    protected abstract MappingStore getCommitLevelFullMatch();
}
