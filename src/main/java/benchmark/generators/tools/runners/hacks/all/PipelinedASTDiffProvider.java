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
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2024-05-13*/
public abstract class PipelinedASTDiffProvider extends ProjectGumTreeASTDiffProvider {
    protected final TreeModifier treeModifier;
    protected final GumTreeMultiMappingMatcher multiMappingMatcher;

    public PipelinedASTDiffProvider(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, GumTreeMultiMappingMatcher multiMappingMatcher, ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(projectMatcher, projectASTDiff, input, caseInfo, configuration, matcher);
        this.treeModifier = treeModifier;
        this.multiMappingMatcher = multiMappingMatcher;
    }
    @Override
    public String  matcherID() {
        String result = getPipelineName() + "_" + super.matcherID();
        if (treeModifier != null)
            result += "_" + treeModifier.getClass().getSimpleName();
        if (multiMappingMatcher != null)
            result += "_" + multiMappingMatcher.getClass().getSimpleName();
        return result;

    }
    abstract String getPipelineName();

    @Override
    public Iterable<Mapping> getFullMatch(Tree srcPT, Tree dstPT) {
        Map<Tree, Tree> srcMap = new LinkedHashMap<>();
        Map<Tree, Tree> dstMap = new LinkedHashMap<>();
        Tree srcCopy = TreeUtilFunctions.deepCopyWithMap(srcPT, srcMap);
        Tree dstCopy = TreeUtilFunctions.deepCopyWithMap(dstPT, dstMap);
        ExtendedMultiMappingStore multimatch = matchTreeCopies(srcCopy, dstCopy);
        Collection<Mapping> result = new ArrayList<>();
        for (Mapping mapping : multimatch) {
            if (mapping.first instanceof FakeTree || mapping.second instanceof FakeTree)
                continue;
            Tree a = srcMap.get(mapping.first);
            Tree b = dstMap.get(mapping.second);
            if (a == null || b == null)
                throw new RuntimeException("Mapping not found in the original trees");
            result.add(new Mapping(a, b));
        }
        return result;
    }

    protected MappingStore modifyTreeAndApplyProjectMatcher(Tree srcCopy, Tree dstCopy) {
        //Modifier
        treeModifier.modify(srcCopy);
        treeModifier.modify(dstCopy);

        //InterFileMappings
        Iterable<Mapping> commitLevelFullMatch = projectMatcher.getCommitLevelFullMatch(srcCopy, dstCopy, matcher);
        MappingStore mappingStore = new MappingStore(srcCopy, dstCopy);
        for (Mapping m : commitLevelFullMatch) mappingStore.addMapping(m.first, m.second);
        return mappingStore;
    }

    abstract ExtendedMultiMappingStore matchTreeCopies(Tree srcCopy, Tree dstCopy);


}