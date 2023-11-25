package benchmark.metrics.computers.filters;

import benchmark.oracle.models.HumanReadableDiff;

public class NoIntraFileFilter implements HumanReadableDiffFilter {
    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        return HumanReadableDiffFilter.noIntraFile(original);
    }
}
