package benchmark;

import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

/* Created by pourya on 2024-04-28*/
public class Runner {
    public static void run(Set<Configuration> confs, boolean rm_only, Consumer<BenchmarkHumanReadableDiffGenerator> consumer) throws Exception {
        for (Configuration configuration : confs) {
            if (rm_only) configuration.setActiveTools(Set.of(ASTDiffTool.GOD, ASTDiffTool.TRV, ASTDiffTool.RMD, ASTDiffTool.RM2));
            consumer.accept(new BenchmarkHumanReadableDiffGenerator(configuration));
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration);
            Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
            MetricsCsvWriter.exportToCSV(stats, configuration.getOutputFolder() + configuration.getName() + ".csv", true);
        }
    }

    public static void main(String[] args) throws Exception {
        run(Set.of(ConfigurationFactory.refOracle()), false,
                benchmarkHumanReadableDiffGenerator -> {
                    try {
                        benchmarkHumanReadableDiffGenerator.generateMultiThreaded();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
