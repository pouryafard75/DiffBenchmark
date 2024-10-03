package benchmark.metrics.computers;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.metrics.models.BaseDiffComparisonResult;

import java.io.IOException;
import java.util.Collection;

public interface StatsComputer {
    Collection<? extends BaseDiffComparisonResult> compute() throws IOException;
    Collection<? extends BaseDiffComparisonResult> compute(IBenchmarkCase IBenchmarkCase) throws IOException;
}
