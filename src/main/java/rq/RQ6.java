package rq;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;

import java.util.List;

/* Created by pourya on 2023-11-23 9:47 p.m. */

/***
 * What is the overall accuracy of each tool?
 */
public class RQ6 implements RQProvider{
    MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.NO_FILTER;
    MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;

    public void rq6(Configuration configuration) throws Exception
    {
//        for (Boolean aBoolean : Set.of(true, false))
//        {
        BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(configuration);
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats(mappingsLocationFilter, mappingsTypeFilter);
        new MetricsCsvWriter(configuration, stats, mappingsLocationFilter, mappingsTypeFilter).writeStatsToCSV(false);
//        }
    }

    @Override
    public void run(Configuration configuration) {
        try {
            rq6(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
