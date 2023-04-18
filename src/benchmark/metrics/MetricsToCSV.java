package benchmark.metrics;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.models.DiffComparisonResult;
import java.util.List;

import static benchmark.metrics.computers.BenchmarkMetricsComputer.writeStatsToCSV;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
        BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer();
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats();
        String[] activeTools = benchmarkMetricsComputer.getActiveTools();
        writeStatsToCSV(stats,activeTools);
    }
}
