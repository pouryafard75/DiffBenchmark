package rq.adb.literature;
//
//import benchmark.data.exp.ExperimentsEnum;
//import benchmark.data.exp.IExperiment;
//import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
//import benchmark.metrics.computers.filters.FilterDuringGeneration;
//import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
//import rq.adb.BaseRQDriverRoutine;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import rq.adb.BaseRQDriverRoutine;

/* Created by pourya on 2025-01-12*/
public class RQDriver {
    private static final HumanReadableDiffFilter[] FILTERS = {
            FilterDuringGeneration.NO_FILTER,
            FilterDuringGeneration.INTRA_FILE_ONLY
    };
    public static ExperimentsEnum exp = ExperimentsEnum.LITERATURE_EXP;

    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(exp).generateMultiThreaded();
        new BaseRQDriverRoutine(FILTERS).run(new IExperiment[]{exp});
        new java.util.Scanner(System.in).nextLine();
    }


}
