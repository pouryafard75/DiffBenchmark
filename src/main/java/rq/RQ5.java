package rq;

import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import static benchmark.metrics.computers.filters.FilterDuringGeneration.INTER_FILE_ONLY;
import static benchmark.metrics.computers.filters.FilterDuringMetricsCalculation.NO_FILTER;

/* Created by pourya on 2023-11-23 7:47 p.m. */

/***
 * How many mappings are missed or mismatched by each tool due to the lack of commit-level change analysis?
 */
public class RQ5 implements RQ{
    private FilterDuringGeneration locationFilter = INTER_FILE_ONLY;
    private FilterDuringMetricsCalculation typeFilter = NO_FILTER;

    public void setLocationFilter(FilterDuringGeneration locationFilter) {
        this.locationFilter = locationFilter;
    }

    public void setTypeFilter(FilterDuringMetricsCalculation typeFilter) {
        this.typeFilter = typeFilter;
    }

    private static void rq5(IExperiment[] experiments, FilterDuringGeneration locationFilter, FilterDuringMetricsCalculation typeFilter) throws IOException {

        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (IExperiment experiment : experiments) {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(experiment, locationFilter.getFilter(), typeFilter);
            stats.addAll(computer.compute());
            name.append(experiment.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq5-" + name + "-" + typeFilter.name() + ".csv", false);
    }
    @Override
    public void run(IExperiment[] experiments) throws Exception {
        rq5(experiments, locationFilter, typeFilter);
    }
}
