package rq;

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.Collection;

/* Created by pourya on 2023-11-23 9:47 p.m. */

/***
 * What is the overall accuracy of each tool?
 */
public class RQ6 implements RQ{
    private MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;
    public void setMappingsTypeFilter(MappingsTypeFilter mappingsTypeFilter) {
        this.mappingsTypeFilter = mappingsTypeFilter;
    }
    public static void rq6(Configuration[] confs, MappingsTypeFilter mappingsTypeFilter1) throws Exception
    {
        for (Configuration configuration : confs) {
            for (MappingsLocationFilter mappingsLocationFilter : new MappingsLocationFilter[]{MappingsLocationFilter.NO_FILTER, MappingsLocationFilter.INTRA_FILE_ONLY}) {
                VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration, mappingsLocationFilter.getFilter(), mappingsTypeFilter1);
                Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
                MetricsCsvWriter.exportToCSV(stats, "rq6-" + configuration.getName() + "-" + mappingsLocationFilter + ".csv", true);
            }
        }
    }
    @Override
    public void run(Configuration[] confs) throws Exception {
        rq6(confs, mappingsTypeFilter);
    }

    public static void main(String[] args) {
        Configuration c1 = ConfigurationFactory.extendedTools_refOracle_3();
        Configuration c2 = ConfigurationFactory.extendedTools_defects4j_3();
        try {
            new RQ6().run(new Configuration[]{c1, c2});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
