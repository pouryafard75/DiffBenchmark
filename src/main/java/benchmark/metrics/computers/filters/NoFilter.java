package benchmark.metrics.computers.filters;

import benchmark.models.hrd.HumanReadableDiff;

public class NoFilter implements HumanReadableDiffFilter {
    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        return original;
    }
}
