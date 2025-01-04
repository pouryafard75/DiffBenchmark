package rq;

/* Created by pourya on 2023-11-20 11:28 a.m. */

import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

/* Created by pourya on 2023-09-19 6:18 p.m. */

/***
 * How many multi-mappings are missed by each tool?
 */
public class RQ1 implements RQ{
    private FilterDuringGeneration filterDuringGeneration = FilterDuringGeneration.MULTI_ONLY;
    private FilterDuringMetricsCalculation filterDuringMetricsCalculation = FilterDuringMetricsCalculation.NO_FILTER;

    public void setMappingsTypeFilter(FilterDuringMetricsCalculation filterDuringMetricsCalculation) {
        this.filterDuringMetricsCalculation = filterDuringMetricsCalculation;
    }

    public void setMappingsLocationFilter(FilterDuringGeneration filterDuringGeneration) {
        this.filterDuringGeneration = filterDuringGeneration;
    }

    @Override
    public void run(IExperiment[] experiments) {
        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();
        StringBuilder name = new StringBuilder();
        for (IExperiment experiment : experiments) {
            try {
                stats.addAll(new VanillaBenchmarkComputer(experiment, filterDuringGeneration.getFilter(), filterDuringMetricsCalculation).compute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            name.append(experiment.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq1-" + name + ".csv", false, "out/");

    }
}

