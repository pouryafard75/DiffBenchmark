package rq;

import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.util.Collection;
import java.util.LinkedHashSet;

/* Created by pourya on 2023-11-23 10:00 p.m. */

/***
 * What is the accuracy of each tool in matching program elements (i.e., method, field declarations)?
 */
public class RQ3 implements RQ{
    private static final FilterDuringGeneration FILTER_DURING_GENERATION = FilterDuringGeneration.NO_FILTER;
    private final FilterDuringMetricsCalculation typeFilter;
    public RQ3(FilterDuringMetricsCalculation typeFilter) {
        this.typeFilter = typeFilter;
    }

    private static void rq3(IExperiment[] experiments, FilterDuringMetricsCalculation typeFilter) throws Exception {
        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (IExperiment experiment : experiments) {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(experiment, FILTER_DURING_GENERATION.getFilter(), typeFilter);
            stats.addAll(computer.compute());
            name.append(experiment.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq3-" + name + "-" + typeFilter.name() + ".csv", false, "out/");
    }
    @Override
    public void run(IExperiment[] experiments) throws Exception {
        rq3(experiments, typeFilter);
    }
}
