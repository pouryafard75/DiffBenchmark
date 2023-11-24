package RQs;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.MappingsToConsider;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;

import java.util.List;

/* Created by pourya on 2023-11-23 7:47 p.m. */
/***
 * How many mappings are missed or mismatched by each tool due to the lack of commit-level change analysis?
 */
public class RQ5 implements RQProvider{

    private final MappingsToConsider mappingsToConsider = MappingsToConsider.INTRA_FILE_ONLY;
    private String csvDestinationFile;

    public void setCsvDestinationFile(String csvDestinationFile) {
        this.csvDestinationFile = csvDestinationFile;
    }

    @Override
    public void run(Configuration configuration) {
        try {
            rq5(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void rq5(Configuration configuration) throws Exception {
        BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(
                configuration);
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats(mappingsToConsider);
        new MetricsCsvWriter(configuration, stats , mappingsToConsider).writeStatsToCSV(false);
    }
}
