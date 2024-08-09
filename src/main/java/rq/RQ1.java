package rq;

/* Created by pourya on 2023-11-20 11:28 a.m. */

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

/* Created by pourya on 2023-09-19 6:18 p.m. */

/***
 * How many multi-mappings are missed by each tool?
 */
public class RQ1 implements RQ{
    private MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.MULTI_ONLY;
    private MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;

    public void setMappingsTypeFilter(MappingsTypeFilter mappingsTypeFilter) {
        this.mappingsTypeFilter = mappingsTypeFilter;
    }

    public void setMappingsLocationFilter(MappingsLocationFilter mappingsLocationFilter) {
        this.mappingsLocationFilter = mappingsLocationFilter;
    }

    @Override
    public void run(Configuration[] confs) {
        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (Configuration configuration : confs) {
            try {
                stats.addAll(new VanillaBenchmarkComputer(configuration, mappingsLocationFilter.getFilter(), mappingsTypeFilter).compute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            name.append(configuration.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq1-" + name + ".csv", false);

    }
}

