package benchmark.manupilator;

import benchmark.DiffManipulator;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.runners.manipulator.BaseBenchmarkCaseDiffManipulator;
import benchmark.utils.Experiments.IQuerySelector;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.function.Consumer;

/* Created by pourya on 2025-01-07*/
public class BenchmarkCaseDiffManipulatorImpl extends BaseBenchmarkCaseDiffManipulator {
    @SafeVarargs
    public BenchmarkCaseDiffManipulatorImpl(IBenchmarkCase benchmarkCase,
                                            Pair<IQuerySelector, Consumer<DiffManipulator>>... commands) throws Exception
    {
        super(benchmarkCase);
        for (Pair<IQuerySelector, Consumer<DiffManipulator>> pair : commands) {
            IQuerySelector selector = pair.getLeft();
            memorize(selector, getManipulated(selector, pair.getRight()));
        }
    }

    private ASTDiff getManipulated(IQuerySelector selector, Consumer<DiffManipulator> consumer) throws Exception {
        DiffManipulator diffManipulator = new DiffManipulator(benchmarkCase, selector);
        consumer.accept(diffManipulator);
        return diffManipulator.getASTDiff();
    }
}
