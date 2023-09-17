package benchmark.metrics;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.utils.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
        BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(
                Configuration.ConfigurationFactory.getDefault());
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats();
        benchmarkMetricsComputer.writeStatsToCSV(stats);
        ObjectMapper mapper = new ObjectMapper();
    }
}
