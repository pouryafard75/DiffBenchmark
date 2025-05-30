package rq.adb.minibattles;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import rq.adb.BaseRQDriverRoutine;

import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2025-01-12*/
public class InterFileRQDriver {
    private static final ExperimentsEnum experiment = ExperimentsEnum.INTER_FILE_EXP;
    private static final HumanReadableDiffFilter[] FILTERS = new HumanReadableDiffFilter[]{
//            FilterDuringGeneration.NO_FILTER,
            FilterDuringGeneration.INTER_FILE_ONLY
    };
    public static void main(String[] args) throws Exception {
//        new BenchmarkHumanReadableDiffGenerator(experiment).generateMultiThreaded();
        experiment.setTools(
                new LinkedHashSet<>(){{
                    this.add(ASTDiffToolEnum.RMD);
                    this.add(ASTDiffToolEnum.EXT_SVN_G);
                    this.add(ASTDiffToolEnum.EXT_SVN_S);
                    this.add(ASTDiffToolEnum.EXT_STM_G);
                    this.add(ASTDiffToolEnum.EXT_STM_S);
                }}
        );
        new BaseRQDriverRoutine(FILTERS, Set.of()).run(new IExperiment[]{experiment});
    }
}
