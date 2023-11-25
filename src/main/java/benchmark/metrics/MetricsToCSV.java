package benchmark.metrics;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.List;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
//        for (Boolean aBoolean : Set.of(true, false))
        {
            Configuration configuration = ConfigurationFactory.defects4j();
            MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.NO_FILTER;
            MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;

            BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(
                    configuration);
            List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats(mappingsLocationFilter, mappingsTypeFilter);
            new MetricsCsvWriter(configuration, stats, mappingsLocationFilter, mappingsTypeFilter).writeStatsToCSV(false);
        }
    }
}
