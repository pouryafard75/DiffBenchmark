package rq;

import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.data.exp.ExperimentConfiguration;

import java.util.Collection;
import java.util.LinkedHashSet;

/* Created by pourya on 2023-11-23 10:00 p.m. */

/***
 * What is the accuracy of each tool in matching program elements (i.e., method, field declarations)?
 */
public class RQ3 implements RQ{
    private static final MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.NO_FILTER;
    private final MappingsTypeFilter typeFilter;
    public RQ3(MappingsTypeFilter typeFilter) {
        this.typeFilter = typeFilter;
    }

    private static void rq3(IExperiment[] experiments, MappingsTypeFilter typeFilter) throws Exception {
        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (IExperiment experiment : experiments) {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(experiment, mappingsLocationFilter.getFilter(), typeFilter);
            stats.addAll(computer.compute());
            name.append(experiment.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq3-" + name + "-" + typeFilter.name() + ".csv", false);
    }
    @Override
    public void run(IExperiment[] experiments) throws Exception {
        rq3(experiments, typeFilter);
    }
}
