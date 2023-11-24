package benchmark.metrics.computers;
import benchmark.metrics.models.Stats;
import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

/* Created by pourya on 2023-04-03 3:53 a.m. */
public class DiffMetricsComputer {

    private final HumanReadableDiff godDiff;
    private final HumanReadableDiff toolDiff;
    private MappingsToConsider mappingsToConsider;

    public DiffMetricsComputer(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, MappingsToConsider mappingsToConsider) {
        this.godDiff = godDiff;
        this.toolDiff = toolDiff;
        this.mappingsToConsider = mappingsToConsider;
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
        Collection<AbstractMapping> godFinalized;
        Collection<AbstractMapping> toolFinalized;
        switch (mappingsToConsider) {
            case ALL:
            case MULTI_ONLY:
                godFinalized = getMerged(godDiff, criteriaSelector, true);
                toolFinalized = getMerged(toolDiff, criteriaSelector, true);
                break;
            case INTER_FILE_ONLY:
                godFinalized = criteriaSelector.apply(godDiff.intraFileMappings);
                toolFinalized = criteriaSelector.apply(toolDiff.intraFileMappings);
                break;
            case INTRA_FILE_ONLY:
                godFinalized = getMerged(godDiff, criteriaSelector, false);
                toolFinalized = getMerged(toolDiff, criteriaSelector, false);
                break;
            default:
                throw new RuntimeException("Not Valid status for mappingsToConsider");
        }
        return makeStats(godFinalized, toolFinalized);
    }

    private static Set<AbstractMapping> getMerged(HumanReadableDiff humanReadableDiff, Function<NecessaryMappings, Collection<AbstractMapping>> criteriaSelector, boolean withIntraFiles) {
        Collection<AbstractMapping> merged =
                withIntraFiles ?
                        criteriaSelector.apply(humanReadableDiff.intraFileMappings) :
                        new HashSet<>();
        for (Map.Entry<String, NecessaryMappings> entry : humanReadableDiff.interFileMappings.entrySet()) {
            merged.addAll(criteriaSelector.apply(entry.getValue()));
        }
        if (merged instanceof Set)
            return ((Set<AbstractMapping>) merged);
        else
            throw new RuntimeException("Bug");
    }
}

