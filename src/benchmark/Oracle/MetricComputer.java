package benchmark.Oracle;

import java.util.List;

/* Created by pourya on 2023-04-03 3:53 a.m. */
public class MetricComputer {

    private final HumanReadableDiff godDiff;
    private final HumanReadableDiff toolDiff;

    public MetricComputer(HumanReadableDiff godDiff, HumanReadableDiff toolDiff) {
        this.godDiff = godDiff;
        this.toolDiff = toolDiff;

    }
    private Stats makeStats(List<AbstractMapping> godList, List<AbstractMapping> toolList) {
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
        return makeStats(godDiff.matchedElements,toolDiff.matchedElements);
    }
    public Stats mappingStats(){
        return makeStats(godDiff.mappings,toolDiff.mappings);
    }

}

