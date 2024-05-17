package rq;

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import static benchmark.metrics.computers.filters.MappingsLocationFilter.INTER_FILE_ONLY;
import static benchmark.metrics.computers.filters.MappingsTypeFilter.NO_FILTER;

/* Created by pourya on 2023-11-23 7:47 p.m. */

/***
 * How many mappings are missed or mismatched by each tool due to the lack of commit-level change analysis?
 */
public class RQ5 implements RQ{
    private MappingsLocationFilter locationFilter = INTER_FILE_ONLY;
    private MappingsTypeFilter typeFilter = NO_FILTER;

    public void setLocationFilter(MappingsLocationFilter locationFilter) {
        this.locationFilter = locationFilter;
    }

    public void setTypeFilter(MappingsTypeFilter typeFilter) {
        this.typeFilter = typeFilter;
    }

    private static void rq5(Configuration[] confs, MappingsLocationFilter locationFilter, MappingsTypeFilter typeFilter) throws IOException {

        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (Configuration conf : confs) {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(conf, locationFilter.getFilter(), typeFilter);
            stats.addAll(computer.compute());
            name.append(conf.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq5-" + name + "-" + typeFilter.name() + ".csv", false);
    }
    @Override
    public void run(Configuration[] confs) throws Exception {
        rq5(confs, locationFilter, typeFilter);
    }
}
