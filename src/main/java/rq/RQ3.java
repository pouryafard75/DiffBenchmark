package rq;

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.Collection;
import java.util.LinkedHashSet;

/* Created by pourya on 2023-11-23 10:00 p.m. */

/***
 * What is the accuracy of each tool in matching program elements (i.e., method, field declarations)?
 */
public class RQ3{

    private static final MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.NO_FILTER;


    public void run(Configuration configuration) {
        throw new RuntimeException("Not implemented yet");
    }

    private void rq3(Configuration configuration, MappingsTypeFilter mappingsTypeFilter) throws Exception {
        VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration, mappingsLocationFilter.getFilter(), mappingsTypeFilter);
        Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
        MetricsCsvWriter.exportToCSV(stats, "rq3.csv", true);
//            new MetricsCsvWriter(computer, stats).write(false);
    }

    public void rq3(Configuration[] confs, MappingsTypeFilter typeFilter) throws Exception {
        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (Configuration conf : confs) {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(conf, mappingsLocationFilter.getFilter(), typeFilter);
            stats.addAll(computer.compute());
            name.append(conf.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq3-" + name + "-" + typeFilter.name() + ".csv", false);
    }

    public static void main(String[] args) throws Exception {
        RQ3 rq3 = new RQ3();
        rq3.rq3(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne(), ConfigurationFactory.defects4jTwoPointOne()}, MappingsTypeFilter.PROGRAM_ELEMENTS);
        rq3.rq3(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne(), ConfigurationFactory.defects4jTwoPointOne()}, MappingsTypeFilter.FIELD_DECL_ONLY);
        rq3.rq3(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne(), ConfigurationFactory.defects4jTwoPointOne()}, MappingsTypeFilter.METHOD_DECL_ONLY);
        rq3.rq3(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne(), ConfigurationFactory.defects4jTwoPointOne()}, MappingsTypeFilter.TYPE_DECL_ONLY);
        rq3.rq3(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne(), ConfigurationFactory.defects4jTwoPointOne()}, MappingsTypeFilter.ENUM_DECL_ONLY);
    }
}
