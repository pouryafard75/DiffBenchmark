package benchmark.metrics.computers;
import benchmark.metrics.models.Stats;
import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;

import java.util.Set;

/* Created by pourya on 2023-04-03 3:53 a.m. */
public class DiffMetricsComputer {

    private final HumanReadableDiff godDiff;
    private final HumanReadableDiff toolDiff;

    public DiffMetricsComputer(HumanReadableDiff godDiff, HumanReadableDiff toolDiff) {
        this.godDiff = godDiff;
        this.toolDiff = toolDiff;

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
        return makeStats(godDiff.intraFileMappings.getMatchedElements(),toolDiff.intraFileMappings.getMatchedElements());
    }
    public Stats mappingStats(){
        return makeStats(godDiff.intraFileMappings.getMappings(),toolDiff.intraFileMappings.getMappings());
    }

}

