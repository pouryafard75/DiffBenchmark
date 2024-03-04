package rq;

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.oracle.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.Collection;

/* Created by pourya on 2023-11-23 9:47 p.m. */

/***
 * What is the overall accuracy of each tool?
 */
public class RQ6{

    private final MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;

    public void rq6(Configuration configuration) throws Exception
    {
        for (MappingsLocationFilter mappingsLocationFilter : new MappingsLocationFilter[]{MappingsLocationFilter.NO_FILTER, MappingsLocationFilter.INTRA_FILE_ONLY}) {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration, mappingsLocationFilter.getFilter(), mappingsTypeFilter);
            Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
            MetricsCsvWriter.exportToCSV(stats, "rq6-" + configuration.getName() + "-" + mappingsLocationFilter + ".csv", true);
        }
    }

    public void run(Configuration configuration) {
        try {
            rq6(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception {
//        new RQ6().rq6(ConfigurationFactory.dummy());
//        new RQ6().rq6(ConfigurationFactory.dummyTwoPointOne());
//        new RQ6().rq6(ConfigurationFactory.refOracle());
//        new RQ6().rq6(ConfigurationFactory.refOracleTwoPointOne());
//        new RQ6().rq6(ConfigurationFactory.defects4j());
        new RQ6().rq6(ConfigurationFactory.defects4jTwoPointOne());
    }
}
