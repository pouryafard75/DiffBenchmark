package benchmark.generators.tools.runners.manipulator;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.models.selector.DiffSelector;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.function.Consumer;

/* Created by pourya on 2025-01-07*/
public class BenchmarkCaseDiffManipulatorImpl extends BaseBenchmarkCaseDiffManipulator {
    @SafeVarargs
    public BenchmarkCaseDiffManipulatorImpl(IBenchmarkCase benchmarkCase,
                                            Pair<DiffSelector, Consumer<DiffManipulator>>... commands) throws Exception
    {
        super(benchmarkCase);
        for (Pair<DiffSelector, Consumer<DiffManipulator>> pair : commands) {
            DiffSelector selector = pair.getLeft();
            memorize(selector, getManipulated(selector, pair.getRight()));
        }
    }

    private ASTDiff getManipulated(DiffSelector selector, Consumer<DiffManipulator> consumer) throws Exception {
        DiffManipulator diffManipulator = new DiffManipulator(benchmarkCase, selector);
        consumer.accept(diffManipulator);
        return diffManipulator.getASTDiff();
    }
}
