package benchmark.metrics.computers.filters;

import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;

public class NoIntraFileFilter implements HumanReadableDiffFilter {
    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        return new HumanReadableDiff(new NecessaryMappings(), original.getInterFileMappings());
    }
}
