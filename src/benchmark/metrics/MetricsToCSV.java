package benchmark.metrics;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static benchmark.metrics.computers.BenchmarkMetricsComputer.writeStatsToCSV2;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
        BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(
                Configuration.ConfigurationFactory.current());
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats();
        String[] activeTools = benchmarkMetricsComputer.getActiveTools();
        System.out.println(Arrays.toString(activeTools));
        writeStatsToCSV2(stats,activeTools);
    }
}
