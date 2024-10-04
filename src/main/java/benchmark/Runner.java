package benchmark;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/* Created by pourya on 2024-04-28*/
public class Runner {
    public static void run(Set<IExperiment> exps, boolean rm_only, Consumer<BenchmarkHumanReadableDiffGenerator> consumer) throws Exception {
        for (IExperiment exp : exps) {
//            if (rm_only) exp.setActiveTools(Set.of(ASTDiffTool.GOD, ASTDiffTool.TRV, ASTDiffTool.RMD, ASTDiffTool.RM2));
            consumer.accept(new BenchmarkHumanReadableDiffGenerator(exp));
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(exp);
            Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
            MetricsCsvWriter.exportToCSV(stats, exp.getOutputFolder() + exp.getName() + ".csv", true);
        }
    }

    public static void main(String[] args) throws Exception {
        run(Set.of(ExperimentsEnum.REF_EXP_3_0), false,
                benchmarkHumanReadableDiffGenerator -> {
                    try {
                        benchmarkHumanReadableDiffGenerator.generateMultiThreaded();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
