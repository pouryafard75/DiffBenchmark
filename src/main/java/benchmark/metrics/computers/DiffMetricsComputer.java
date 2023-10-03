package benchmark.metrics.computers;
import benchmark.metrics.models.Stats;
import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* Created by pourya on 2023-04-03 3:53 a.m. */
public class DiffMetricsComputer {

    private final HumanReadableDiff godDiff;
    private final HumanReadableDiff toolDiff;
    private final boolean includingInterFileMappings;

    public DiffMetricsComputer(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, boolean includingInterFileMappings) {
        this.godDiff = godDiff;
        this.toolDiff = toolDiff;
        this.includingInterFileMappings = includingInterFileMappings;
    }
    private Stats makeStats(Set<AbstractMapping> godList, Set<AbstractMapping> toolList) {
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
        if (!includingInterFileMappings)
            return makeStats(
                godDiff.intraFileMappings.getMatchedElements(),
                toolDiff.intraFileMappings.getMatchedElements()
            );
        else {
            Set<AbstractMapping> godMerged = getMerged(godDiff, NecessaryMappings::getMatchedElements);
            Set<AbstractMapping> toolMerged = getMerged(toolDiff, NecessaryMappings::getMatchedElements);
            return makeStats(godMerged,toolMerged);
        }
    }
    public Stats mappingStats(){
        if (!includingInterFileMappings)
            return makeStats(godDiff.intraFileMappings.getMappings(),toolDiff.intraFileMappings.getMappings());
        else {
            Set<AbstractMapping> godMerged = getMerged(godDiff, NecessaryMappings::getMappings);
            Set<AbstractMapping> toolMerged = getMerged(toolDiff, NecessaryMappings::getMappings);
            return makeStats(godMerged,toolMerged);
        }
    }

    private static Set<AbstractMapping> getMerged(HumanReadableDiff humanReadableDiff, Function<NecessaryMappings,Set<AbstractMapping>> function) {
        Set<AbstractMapping> merged = function.apply(humanReadableDiff.intraFileMappings);
        for (Map.Entry<String, NecessaryMappings> stringNecessaryMappingsEntry : humanReadableDiff.interFileMappings.entrySet()) {
            merged.addAll(function.apply(stringNecessaryMappingsEntry.getValue()));
        }
        return merged;
    }

}

