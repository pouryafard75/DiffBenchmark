package benchmark.metrics.computers;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.Stats;
import benchmark.models.AbstractMapping;
import benchmark.models.HumanReadableDiff;
import benchmark.models.NecessaryMappings;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/* Created by pourya on 2023-04-03 3:53 a.m. */
public class DiffMetricsComputer {

    private final HumanReadableDiff godDiff;
    private final HumanReadableDiff toolDiff;
    private MappingsTypeFilter mappingsTypeFilter; //Too many delegations, looks smelly

    public DiffMetricsComputer(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, MappingsTypeFilter mappingsTypeFilter) {
        this.godDiff = godDiff;
        this.toolDiff = toolDiff;
        this.mappingsTypeFilter = mappingsTypeFilter;
    }
    private Stats makeStats(Collection<AbstractMapping> godList, Collection<AbstractMapping> toolList) {
        int TP = 0;
        int FP = 0;
        int FN = 0;
        for (AbstractMapping mapping : godList) {
            if (toolList.contains(mapping))
                TP += 1;
            else
                FN += 1;
        }
        for (AbstractMapping mapping : toolList) {
            if (!godList.contains(mapping))
                FP += 1;
        }
//        if (TP > ignores)
//            TP = TP - ignores;
        return new Stats(TP, FP, FN);
    }

    public Stats programElementStats(){
        return categoyStats(NecessaryMappings::getMatchedElements);
    }
    public Stats mappingStats(){
        return categoyStats(NecessaryMappings::getMappings);
    }
    public Stats categoyStats(Function<NecessaryMappings, Collection<AbstractMapping>> criteriaSelector) {
        Collection<AbstractMapping> godFinalized = criteriaSelector.apply(flatten(godDiff).getIntraFileMappings());
        Collection<AbstractMapping> toolFinalized = criteriaSelector.apply(flatten(toolDiff).getIntraFileMappings());
        return makeStats(
                mappingsTypeFilter.apply(godFinalized),
                mappingsTypeFilter.apply(toolFinalized)
        );
    }

    private static HumanReadableDiff flatten(HumanReadableDiff humanReadableDiff) {
        return new HumanReadableDiff(
                new NecessaryMappings(
                        flatten(humanReadableDiff, NecessaryMappings::getMatchedElements),
                        flatten(humanReadableDiff, NecessaryMappings::getMappings)
                )
        );
    }
    private static Collection<AbstractMapping> flatten(HumanReadableDiff humanReadableDiff, Function<NecessaryMappings, Collection<AbstractMapping>> criteriaSelector) {
        Collection<AbstractMapping> mergedResult = criteriaSelector.apply(humanReadableDiff.getIntraFileMappings());
        for (Map.Entry<String, NecessaryMappings> entry : humanReadableDiff.getInterFileMappings().entrySet())
            mergedResult.addAll(criteriaSelector.apply(entry.getValue()));
        return mergedResult;
    }
}

