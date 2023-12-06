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

/* Created by pourya on 2023-11-23 7:47 p.m. */

/***
 * How many mappings are missed or mismatched by each tool due to the lack of commit-level change analysis?
 */
public class RQ5{

    public void run(Configuration configuration) {
        try {
            rq5(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void rq5(Configuration configuration) throws Exception {
        throw new RuntimeException("Not implemented yet");
//        VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration, mappingsLocationFilter.getFilter(), mappingsTypeFilter);
//        Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
////        new MetricsCsvWriter(configuration, stats , mappingsLocationFilter, mappingsTypeFilter).writeStatsToCSV(false);
////        new MetricsCsvWriter(computer,stats).write(false);
//        MetricsCsvWriter.exportToCSV(stats, "rq5.csv",true);
    }
    private void rq5(Configuration[] confs) throws IOException {

        MappingsLocationFilter locationFilter = MappingsLocationFilter.INTER_FILE_ONLY;
        MappingsTypeFilter typeFilter = MappingsTypeFilter.NO_FILTER;

        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (Configuration conf : confs) {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(conf, locationFilter.getFilter(), typeFilter);
            stats.addAll(computer.compute());
            name.append(conf.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq5-" + name + "-" + typeFilter.name() + ".csv", false);
    }

    public static void main(String[] args) throws IOException {
        new RQ5().rq5(new Configuration[]{ConfigurationFactory.refOracle(), ConfigurationFactory.defects4j()});
        new RQ5().rq5(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne(), ConfigurationFactory.defects4jTwoPointOne()});
    }
}
