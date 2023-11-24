package benchmark.metrics;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.MappingsToConsider;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
//        for (Boolean aBoolean : Set.of(true, false))
        {
            Configuration configuration = ConfigurationFactory.refOracle();
            MappingsToConsider mappingsToConsider = MappingsToConsider.ALL;

            BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(
                    configuration);
            List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats(mappingsToConsider);
            new MetricsCsvWriter(configuration, stats, mappingsToConsider).writeStatsToCSV(false);
        }
    }
}
