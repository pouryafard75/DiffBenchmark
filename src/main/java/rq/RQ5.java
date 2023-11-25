package rq;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;

import java.util.List;

/* Created by pourya on 2023-11-23 7:47 p.m. */

/***
 * How many mappings are missed or mismatched by each tool due to the lack of commit-level change analysis?
 */
public class RQ5 implements RQProvider{

    private static final MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.INTRA_FILE_ONLY;
    private static final MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;
    private String csvDestinationFile; //TODO

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
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats(mappingsLocationFilter, mappingsTypeFilter);
        new MetricsCsvWriter(configuration, stats , mappingsLocationFilter, mappingsTypeFilter).writeStatsToCSV(false);
    }
}
