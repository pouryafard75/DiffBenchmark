package rq.adb.minibattles;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import rq.adb.BaseRQDriverRoutine;

/* Created by pourya on 2025-01-12*/
public class InterFileRQDriver {
    private static final ExperimentsEnum experiment = ExperimentsEnum.INTER_FILE_EXP;
    private static final HumanReadableDiffFilter[] FILTERS = new HumanReadableDiffFilter[]{
            FilterDuringGeneration.NO_FILTER,
            FilterDuringGeneration.INTER_FILE_ONLY
    };
    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(experiment).generateMultiThreaded();
        new BaseRQDriverRoutine(FILTERS).run(new IExperiment[]{experiment});
    }
}
