package rq.adb.minibattles;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import rq.adb.BaseRQDriverRoutine;

import java.util.Set;

/* Created by pourya x  on 2025-01-12*/
public class SemanticViolationRQDriver {
    private static final HumanReadableDiffFilter[] FILTERS = new HumanReadableDiffFilter[]{
//            FilterDuringGeneration.NO_FILTER,
            FilterDuringGeneration.INTRA_FILE_ONLY
    };
    public static ExperimentsEnum exp = ExperimentsEnum.SEMANTIC_VIOLATION_EXP;

    public static void main(String[] args) throws Exception {
        new BaseRQDriverRoutine(FILTERS, Set.of(
//                ASTDiffToolEnum.SPN_T,
//                ASTDiffToolEnum.SPN_S_T,
//                ASTDiffToolEnum.SPN_G_T
        ))
        .run(new IExperiment[]{exp});
    }
}
