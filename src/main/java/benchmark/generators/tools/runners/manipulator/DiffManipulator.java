package benchmark.generators.tools.runners.manipulator;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.models.DiffSide;
import benchmark.metrics.computers.violation.models.TriPredicate;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.editscript.SimplifiedExtendedChawatheScriptGenerator;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import java.util.function.BiConsumer;

import static benchmark.generators.tools.runners.Utils.makeASTDiff;

/* Created by pourya on 2025-01-07*/
public class DiffManipulator implements ASTDiffProvider {
    private final IBenchmarkCase benchmarkCase;
    private final DiffSelector querySelector;
    private final ExtendedMultiMappingStore mappings;
    private final ASTDiff fromRM;

    public DiffManipulator(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
        this.benchmarkCase = benchmarkCase;
        this.querySelector = querySelector;
        fromRM = querySelector.apply(benchmarkCase.getProjectASTDiff());
        mappings = new ExtendedMultiMappingStore(fromRM.src.getRoot(), fromRM.dst.getRoot());
    }


    public void acceptAll(IASTDiffTool iastDiffTool) throws Exception {
        modifyToolWithOffset(DiffSide.LEFT,
                iastDiffTool,
                0, Integer.MAX_VALUE,
                (extendedMultiMappingStore, mapping) ->
                        extendedMultiMappingStore.addMapping(
                                mapping.first,
                                mapping.second),
                (tree, start, end) -> true
        );
    }

    public void accept(DiffSide side, IASTDiffTool iastDiffTool, int startOffset, int endOffset) throws Exception {
        modifyToolWithOffset(side,
                iastDiffTool,
                startOffset,
                endOffset,
                (extendedMultiMappingStore, mapping) ->
                        extendedMultiMappingStore.addMapping(
                                mapping.first,
                                mapping.second),
                                (tree, start, end) -> tree.getPos() >= start && tree.getEndPos() <= end
        );
    }

    public void discard(DiffSide side, IASTDiffTool iastDiffTool, int startOffset, int endOffset) throws Exception {
        modifyToolWithOffset(side,
                iastDiffTool,
                startOffset,
                endOffset,
                (extendedMultiMappingStore, mapping) ->
                        extendedMultiMappingStore.removeMapping(mapping.first, mapping.second), (tree, start, end) -> tree.getPos() >= start && tree.getEndPos() <= end
        );
    }


    public void modifyToolWithOffset(DiffSide side, IASTDiffTool iastDiffTool, int startOffset, int endOffset, BiConsumer<ExtendedMultiMappingStore, Mapping> function, TriPredicate<Tree, Integer, Integer> triPredicate) throws Exception {
        ASTDiff toolsDiff = iastDiffTool.apply(benchmarkCase, querySelector).getASTDiff();
        for (Mapping allMapping : toolsDiff.getAllMappings()) {
            Tree t = side.getTree(allMapping);
            if (triPredicate.test(t, startOffset, endOffset)) {
                function.accept(mappings, allMapping);
            }
        }
    }


    @Override
    public ASTDiff getASTDiff() throws Exception {
        ASTDiff astDiff = makeASTDiff(fromRM, fromRM.src.getRoot(), fromRM.dst.getRoot(), mappings);
        astDiff.computeEditScript(
                benchmarkCase.getProjectASTDiff().getParentContextMap(),
                benchmarkCase.getProjectASTDiff().getChildContextMap(),
                new SimplifiedExtendedChawatheScriptGenerator());
        return astDiff;
    }
}

