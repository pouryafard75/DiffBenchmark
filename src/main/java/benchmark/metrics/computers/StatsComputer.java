package benchmark.metrics.computers;

import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.utils.CaseInfo;

import java.io.IOException;
import java.util.Collection;

public interface StatsComputer {
    Collection<? extends BaseDiffComparisonResult> compute() throws IOException;
    Collection<? extends BaseDiffComparisonResult> compute(CaseInfo info) throws IOException;
}
