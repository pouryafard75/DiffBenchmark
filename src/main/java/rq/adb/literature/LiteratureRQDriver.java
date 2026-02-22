package rq.adb.literature;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;


/* Created by anonymous on 2025-01-12*/
public class LiteratureRQDriver {
    private static final HumanReadableDiffFilter[] FILTERS = {
            FilterDuringGeneration.NO_FILTER,
            FilterDuringGeneration.INTRA_FILE_ONLY
    };
    public static ExperimentsEnum exp = ExperimentsEnum.LITERATURE_EXP;

    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(exp).generateMultiThreaded();
    }


}
