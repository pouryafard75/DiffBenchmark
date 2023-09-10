package benchmark;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.oracle.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.utils.Configuration;

import java.io.IOException;
import java.util.List;

/* Created by pourya on 2023-09-08 6:02 p.m. */
public class MakeAndBench {
    public static void main(String[] args) throws Exception {
        Configuration config = Configuration.ConfigurationFactory.getDefault();

        BenchmarkHumanReadableDiffGenerator benchmarkHumanReadableDiffGenerator =
                new BenchmarkHumanReadableDiffGenerator(config);
        benchmarkHumanReadableDiffGenerator.generate();

        BenchmarkMetricsComputer benchmarkMetricsComputer =
                new BenchmarkMetricsComputer(config);
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats();
        benchmarkMetricsComputer.writeStatsToCSV(stats);
    }

}
