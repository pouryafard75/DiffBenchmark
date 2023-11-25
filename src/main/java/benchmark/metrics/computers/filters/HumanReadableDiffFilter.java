package benchmark.metrics.computers.filters;

import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;

public interface HumanReadableDiffFilter {
    HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack);
    default HumanReadableDiff make(HumanReadableDiff original){
        return make(original, original);
    }

    static HumanReadableDiff noInterFile(HumanReadableDiff humanReadableDiff) {
        return new HumanReadableDiff(humanReadableDiff.getIntraFileMappings());
    }

    static HumanReadableDiff noIntraFile(HumanReadableDiff humanReadableDiff) {
        return new HumanReadableDiff(new NecessaryMappings(), humanReadableDiff.getInterFileMappings());
    }

    static HumanReadableDiff noFilter(HumanReadableDiff humanReadableDiff) {
        return humanReadableDiff;
    }
}
