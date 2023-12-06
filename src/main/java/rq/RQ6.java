package rq;

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;

import java.util.Collection;
import java.util.List;

/* Created by pourya on 2023-11-23 9:47 p.m. */

/***
 * What is the overall accuracy of each tool?
 */
public class RQ6{
    MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.NO_FILTER;
    MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;

    public void rq6(Configuration configuration) throws Exception
    {
        VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration, mappingsLocationFilter.getFilter(), mappingsTypeFilter);
        Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
        MetricsCsvWriter.exportToCSV(stats, "rq6.csv",true);

//        new MetricsCsvWriter(computer, stats).writeStatsToCSV("");
//        new MetricsCsvWriter(configuration, stats, mappingsLocationFilter, mappingsTypeFilter).writeStatsToCSV(false);
    }

    public void run(Configuration configuration) {
        try {
            rq6(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
