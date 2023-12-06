package benchmark.metrics.computers.filters;

import benchmark.oracle.models.HumanReadableDiff;

public interface HumanReadableDiffFilter {
    HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack);
    default HumanReadableDiff make(HumanReadableDiff original) {
        return make(original, original);
    }
}
