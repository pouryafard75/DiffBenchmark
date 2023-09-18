package benchmark.metrics;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
        for (Boolean aBoolean : Set.of(true, false)) {
            BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(
                    ConfigurationFactory.getDefault(), aBoolean);
            List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats();
            benchmarkMetricsComputer.writeStatsToCSV(stats);
        }
    }
}
