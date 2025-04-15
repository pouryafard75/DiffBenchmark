package rq.adb.visitor;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import rq.adb.BaseRQDriverRoutine;

import java.util.Set;

/* Created by pourya on 2025-01-12*/
public class VisitorsImpactRQDriver {
    private static final HumanReadableDiffFilter[] FILTERS = new HumanReadableDiffFilter[]{
//            FilterDuringGeneration.NO_FILTER,
            FilterDuringGeneration.INTRA_FILE_ONLY
    };
    public static ExperimentsEnum exp = ExperimentsEnum.VISITOR_EXP;

    public static void main(String[] args) throws Exception {
//        new BenchmarkHumanReadableDiffGenerator(exp).generateMultiThreaded();
        new BaseRQDriverRoutine(FILTERS, Set.of()).run(new IExperiment[]{exp});
    }
}
