package benchmark.metrics.computers.filters;

import benchmark.models.AbstractMapping;
import benchmark.models.HumanReadableDiff;
import benchmark.models.NecessaryMappings;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/* Created by pourya on 2023-12-09 11:00 p.m. */
public class InterFileRelatedFilter implements HumanReadableDiffFilter {
    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        HumanReadableDiff result = new NoIntraFileFilter().make(original, HumanReadableDiff.makeEmpty());
        if (slack.isEmpty() && slack.equals(original))
            return result;
        addAssociactedInterFileMappingsWithinTheIntraFileMappings(result, original, slack);
        return result;
    }

    private void addAssociactedInterFileMappingsWithinTheIntraFileMappings(HumanReadableDiff result, HumanReadableDiff original, HumanReadableDiff god) {
        for (AbstractMapping mapping : original.getIntraFileMappings().getMappings()) {
            for (Map.Entry<String, NecessaryMappings> entry : god.getInterFileMappings().entrySet()) {
                extracted(result, mapping, entry, NecessaryMappings::getMappings, god.getIntraFileMappings());
                extracted(result, mapping, entry, NecessaryMappings::getMatchedElements, god.getIntraFileMappings());

            }
        }
    }

    private static void extracted(HumanReadableDiff result, AbstractMapping mapping, Map.Entry<String, NecessaryMappings> entry, Function<NecessaryMappings, Collection<AbstractMapping>> criteriaSelector, NecessaryMappings godIntra) {
        String key = entry.getKey();
        NecessaryMappings value = entry.getValue();
        criteriaSelector.apply(value).stream().forEach(abs -> {
            if (key.contains("Moved to"))
            {
                //HRD A : Moved to B , then it was part of the left(src)
                if (abs.getLeftInfo().equals(mapping.getLeftInfo()))
                    if (!acceptable(criteriaSelector.apply(godIntra), mapping))
                        criteriaSelector.apply(result.getIntraFileMappings()).add(mapping);
            }
            else if (key.contains("Moved from"))
            {
                //HRD A : Moved from B , then it was part of the right(dst)
                if (abs.getRightInfo().equals(mapping.getRightInfo()))
                    if (!acceptable(criteriaSelector.apply(godIntra), mapping))
                        criteriaSelector.apply(result.getIntraFileMappings()).add(mapping);

            }
            else
                throw new RuntimeException("Unexpected key in inter-file mappings: " + key);
        });
    }

    private static boolean acceptable(Collection<AbstractMapping> apply, AbstractMapping mapping) {
        return apply.stream().anyMatch(abs ->
                abs.getLeftInfo().equals(
                        mapping.getLeftInfo()) && abs.getRightInfo().equals(mapping.getRightInfo()));
    }
}
