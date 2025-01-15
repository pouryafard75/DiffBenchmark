package rq.adb.literature;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import rq.adb.BaseRQDriverRoutine;

/* Created by pourya on 2025-01-12*/
public class RQDriver {
    private static final HumanReadableDiffFilter[] FILTERS = new HumanReadableDiffFilter[]{
            FilterDuringGeneration.NO_FILTER,
            FilterDuringGeneration.INTRA_FILE_ONLY
    };
    public static void main(String[] args) throws Exception {
        new BaseRQDriverRoutine(FILTERS).run(new IExperiment[]{ExperimentsEnum.LITERATURE_EXP});
    }

}
