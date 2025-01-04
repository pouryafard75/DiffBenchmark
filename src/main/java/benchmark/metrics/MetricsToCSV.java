package benchmark.metrics;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.util.Collection;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
//        for (Boolean aBoolean : Set.of(true, false))
        {
            IExperiment experiment = ExperimentsEnum.INCOMPATIBLE_TOOLS_TRIAL;
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(experiment);
            Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
            MetricsCsvWriter.exportToCSV(stats, experiment.getOutputFolder() + experiment.getName() + ".csv", true, "out/");
//            new MetricsCsvWriter(computer, stats).writeStatsToCSV(false);
        }
    }
}