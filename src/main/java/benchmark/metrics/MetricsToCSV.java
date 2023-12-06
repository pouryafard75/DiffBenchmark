package benchmark.metrics;

import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.CsvWritable;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.Collection;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
//        for (Boolean aBoolean : Set.of(true, false))
        {
            Configuration configuration = ConfigurationFactory.dummy();

            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration);
            Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
            MetricsCsvWriter.exportToCSV(stats, configuration.getOutputFolder() + configuration.getName() + ".csv", true);
//            new MetricsCsvWriter(computer, stats).writeStatsToCSV(false);
        }
    }
}