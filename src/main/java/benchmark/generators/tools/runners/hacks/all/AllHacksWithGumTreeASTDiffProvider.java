package benchmark.generators.tools.runners.hacks.all;

import benchmark.generators.tools.runners.hacks.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.hacks.interfile.ProjectGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.hacks.labels.TreeModifier;
import benchmark.generators.tools.runners.hacks.multimapping.GumTreeMultiMappingMatcher;
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
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2024-05-10*/
public class AllHacksWithGumTreeASTDiffProvider extends ProjectGumTreeASTDiffProvider {
    private final TreeModifier treeModifier;
    private final GumTreeMultiMappingMatcher multiMappingMatcher;

    public AllHacksWithGumTreeASTDiffProvider(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, GumTreeMultiMappingMatcher multiMappingMatcher, ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(projectMatcher, projectASTDiff, input, caseInfo, configuration, matcher);
        this.treeModifier = treeModifier;
        this.multiMappingMatcher = multiMappingMatcher;
    }

    private void populateFakeRoot(Map<Tree, Tree> srcCopy, FakeTree fakeRoot, Collection<TreeContext> contexts) {
        for (TreeContext treeContext : contexts) {
            Tree copy = TreeUtilFunctions.deepCopyWithMap(treeContext.getRoot(), srcCopy);
            fakeRoot.addChild(copy);
        }
    }


    @Override
    public Iterable<Mapping> getFullMatch() {
        FakeTree fakeRootSrc = new FakeTree();
        FakeTree fakeRootDst = new FakeTree();
        Map<Tree, Tree> srcCopy = new LinkedHashMap<>();
        Map<Tree, Tree> dstCopy = new LinkedHashMap<>();
        populateFakeRoot(srcCopy, fakeRootSrc, ptc.values());
        populateFakeRoot(dstCopy, fakeRootDst, ctc.values());
        treeModifier.modify(fakeRootSrc);
        treeModifier.modify(fakeRootDst);
        Iterable<Mapping> commitLevelFullMatch = projectMatcher.getCommitLevelFullMatch(fakeRootSrc, fakeRootDst, matcher);
        MappingStore mappingStore = new MappingStore(fakeRootSrc, fakeRootDst);
        for (Mapping m : commitLevelFullMatch) {
            mappingStore.addMapping(m.first, m.second);
        }
        ExtendedMultiMappingStore multimatch = multiMappingMatcher.multimatch(fakeRootSrc, fakeRootDst, matcher, mappingStore);
        //multimatch contains all the mapping, but we have to find the eqv in the original trees
        Collection<Mapping> result = new ArrayList<>();
        for (Mapping mapping : multimatch) {
            if (mapping.first instanceof FakeTree || mapping.second instanceof FakeTree)
                continue;
            Tree a = srcCopy.get(mapping.first);
            Tree b = dstCopy.get(mapping.second);
            if (a == null || b == null)
                throw new RuntimeException("Mapping not found in the original trees");
            result.add(new Mapping(a, b));
        }
        return result;



    }


    @Override
    public String  matcherID() {
        return super.matcherID() + "_" + treeModifier.getClass().getSimpleName() + "_" + multiMappingMatcher.getClass().getSimpleName();
    }
}
