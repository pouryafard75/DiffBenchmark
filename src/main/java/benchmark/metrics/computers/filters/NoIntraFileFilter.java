package benchmark.metrics.computers.filters;

import benchmark.models.HumanReadableDiff;
import benchmark.models.NecessaryMappings;

public class NoIntraFileFilter implements HumanReadableDiffFilter {
    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        return new HumanReadableDiff(new NecessaryMappings(), original.getInterFileMappings());
    }
}
