package rq;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.util.Collection;

/* Created by pourya on 2023-11-23 9:47 p.m. */

/***
 * What is the overall accuracy of each tool?
 */
public class RQ6 implements RQ{
    private FilterDuringMetricsCalculation filterDuringMetricsCalculation = FilterDuringMetricsCalculation.NO_FILTER;
    public void setMappingsTypeFilter(FilterDuringMetricsCalculation filterDuringMetricsCalculation) {
        this.filterDuringMetricsCalculation = filterDuringMetricsCalculation;
    }
    public static void rq6(IExperiment[] experiments, FilterDuringMetricsCalculation filterDuringMetricsCalculation1) throws Exception
    {
        for (IExperiment experiment : experiments) {
            for (FilterDuringGeneration filterDuringGeneration : new FilterDuringGeneration[]{FilterDuringGeneration.NO_FILTER, FilterDuringGeneration.INTRA_FILE_ONLY}) {
                VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(experiment, filterDuringGeneration.getFilter(), filterDuringMetricsCalculation1);
                Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
                MetricsCsvWriter.exportToCSV(stats, "rq6-" + experiment.getName() + "-" + filterDuringGeneration + ".csv", true, "out/");
            }
        }
    }
    @Override
    public void run(IExperiment[] exps) throws Exception {
        rq6(exps, filterDuringMetricsCalculation);
    }

    public static void main(String[] args) {
        try {
            new RQ6().run(new IExperiment[]{
                    ExperimentsEnum.REF_COMMENTS_3_0,
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
