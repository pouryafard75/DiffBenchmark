package benchmark.generators.tools.runners.hacks.interfile;

import benchmark.generators.tools.runners.converter.AbstractASTDiffProviderFromExportedMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.FakeTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.util.*;
import java.util.function.Function;

/* Created by pourya on 2024-04-30*/
public class ProjectGumTreeASTDiffProvider extends AbstractASTDiffProviderFromExportedMappings {
    protected final Matcher matcher;
    protected final GumTreeProjectMatcher projectMatcher;
    public ProjectGumTreeASTDiffProvider(GumTreeProjectMatcher projectMatcher, ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(projectASTDiff, input, caseInfo, configuration);
        this.matcher = matcher;
        this.projectMatcher = projectMatcher;
    }

    public ProjectGumTreeASTDiffProvider(ProjectGumTreeASTDiffProvider projectGumTreeASTDiff) {
        super(projectGumTreeASTDiff.projectASTDiff, projectGumTreeASTDiff.input, projectGumTreeASTDiff.info, projectGumTreeASTDiff.conf);
        this.matcher = projectGumTreeASTDiff.matcher;
        this.projectMatcher = projectGumTreeASTDiff.projectMatcher;
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
        Tree srcPT = makeVirtualNode(ptc, projectASTDiff.getDiffSet(), ASTDiff::getSrcPath);
        Tree dstPT = makeVirtualNode(ctc, projectASTDiff.getDiffSet(), ASTDiff::getDstPath);
        Iterable<Mapping> match = getFullMatch(srcPT, dstPT);
        revertRoots(ptc.values());
        revertRoots(ctc.values());
        Collection<Mapping> mappings = new LinkedHashSet<>();
        for (Mapping mapping : match) {
            if (belongsTo(mapping.first, ptc.get(input.getSrcPath()).getRoot())
                    ||
                belongsTo(mapping.second, ctc.get(input.getDstPath()).getRoot()))
                    mappings.add(mapping);
        }
        return MappingExportModel.exportModelList(mappings);
    }

    Tree makeVirtualNode(Map<String, TreeContext> treeContextCollection, Set<ASTDiff> diffSet, Function<ASTDiff, String> function) {
        FakeTree root = new FakeTree();
        //Add All ASTDiffs to the root according to RM DiffSet output
        for (ASTDiff astDiff : diffSet) {
            String fileName = function.apply(astDiff);
            TreeContext treeContext = treeContextCollection.get(fileName);
            Tree thisRoot = treeContext.getRoot();
            root.addChild(thisRoot);
        }
        //Add the rest of context which have not been added to the root
        for (TreeContext treeContext : treeContextCollection.values()) {
            if (!root.getChildren().contains(treeContext.getRoot())) {
                root.addChild(treeContext.getRoot());
            }
        }
        //TODO Must be tested but seems pretty damn right
        return root;
    }

    void revertRoots(Collection<TreeContext> treeContextCollection) {
        for (TreeContext treeContext : treeContextCollection) {
            treeContext.getRoot().setParent(null);
        }
    }
    public Iterable<Mapping> getFullMatch(Tree srcPT, Tree dstPT) {
        Iterable<Mapping> result = projectMatcher.getCommitLevelFullMatch(srcPT, dstPT, matcher);
        revertRoots(ptc.values());
        revertRoots(ctc.values());
        return result;
    }
    public String matcherID() {
        return matcher.getClass().getSimpleName() + "_" + projectMatcher.getClass().getSimpleName();
    }
}
