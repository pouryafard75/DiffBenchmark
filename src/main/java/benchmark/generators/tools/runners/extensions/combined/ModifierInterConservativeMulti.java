package benchmark.generators.tools.runners.extensions.combined;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.runners.extensions.interfile.GumTreeProjectMatcher;
import benchmark.generators.tools.runners.extensions.interfile.StagedTreeMatching;
import benchmark.generators.tools.runners.extensions.labels.TreeModifier;
import benchmark.generators.tools.runners.extensions.multimapping.GumTreeMultiMappingMatcher;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-05-13*/
public class ModifierInterConservativeMulti extends PipelinedASTDiffProvider {


    public ModifierInterConservativeMulti(TreeModifier treeModifier, GumTreeProjectMatcher projectMatcher, GumTreeMultiMappingMatcher multiMappingMatcher, IBenchmarkCase benchmarkCase, DiffSelector query, Matcher simpleGumtree) {
        super(treeModifier, projectMatcher, multiMappingMatcher, benchmarkCase, query, simpleGumtree);
    }


    @Override
    String getPipelineName() {
        return "MIPM"; //ModifierInterPartialMulti
    }

    @Override
    ExtendedMultiMappingStore matchTreeCopies(Tree srcCopy, Tree dstCopy) {
        MappingStore mappingStore = modifyTreeAndApplyProjectMatcher(srcCopy, dstCopy);

        //MultiMappings
        ExtendedMultiMappingStore result = new ExtendedMultiMappingStore(srcCopy, dstCopy);
        new StagedTreeMatching(projectASTDiff).iterativeRound(srcCopy, dstCopy,
                (srcChild, dstChild) -> find(srcChild, dstChild, mappingStore, result));
        return result;
    }

    private void find(Tree srcChild, Tree dstChild, MappingStore mappingStore, ExtendedMultiMappingStore result) {
        ExtendedMultiMappingStore multimatch = multiMappingMatcher.multimatch(srcChild, dstChild, matcher, mappingStore);
        for (Mapping mapping : multimatch) {
            result.addMapping(mapping.first, mapping.second);
        }
    }
}
