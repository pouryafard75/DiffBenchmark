package rq.adb.minibattles;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import rq.adb.BaseRQDriverRoutine;

import java.util.Set;

/* Created by pourya on 2025-01-12*/
public class ExtensionSkirmishRQDriver {
    private static final HumanReadableDiffFilter[] FILTERS = new HumanReadableDiffFilter[]{
            FilterDuringGeneration.NO_FILTER,
//            FilterDuringGeneration.INTRA_FILE_ONLY
    };
    public static final ExperimentsEnum exp = ExperimentsEnum.ARTIFICIAL_EXP;

    public static void main(String[] args) throws Exception {
//        new BenchmarkHumanReadableDiffGenerator(exp).generateMultiThreaded();
        new BaseRQDriverRoutine(FILTERS, Set.of()).run(new IExperiment[]{exp});
    }
}
