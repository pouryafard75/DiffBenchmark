package benchmark.metrics.computers.filters;

import benchmark.models.AbstractMapping;
import benchmark.models.HumanReadableDiff;
import benchmark.models.NecessaryMappings;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;

public class MultiMappingsFilter implements HumanReadableDiffFilter {
    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        return new HumanReadableDiff
                (new NecessaryMappings(
                        extractMultiMappings(original, slack, NecessaryMappings::getMatchedElements),
                        extractMultiMappings(original, slack, NecessaryMappings::getMappings)));
    }

    private static Set<AbstractMapping> extractMultiMappings(HumanReadableDiff godDiff, HumanReadableDiff other,
                                                             Function<NecessaryMappings, Collection<AbstractMapping>> function) {
        Set<AbstractMapping> multi = new HashSet<>();
        for (AbstractMapping m1 : function.apply(godDiff.getIntraFileMappings())) {
            for (AbstractMapping m2 : function.apply(other.getIntraFileMappings())) {
                if (m1.isMultiMappingPartner(m2)) {
                    multi.add(m1);
                    break;
                }
            }
        }
        return multi;
    }
}
