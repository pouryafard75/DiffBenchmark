package benchmark.metrics.computers.filters;

import benchmark.oracle.models.HumanReadableDiff;

public class NoInterFileFilter implements HumanReadableDiffFilter {
    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        return new HumanReadableDiff(original.getIntraFileMappings());
    }
}
