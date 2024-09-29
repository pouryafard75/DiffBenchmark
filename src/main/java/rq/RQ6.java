package rq;

import benchmark.data.exp.EExperiment;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

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
    public static void rq6(IExperiment[] experiments, MappingsTypeFilter mappingsTypeFilter1) throws Exception
    {
        for (IExperiment experiment : experiments) {
            for (MappingsLocationFilter mappingsLocationFilter : new MappingsLocationFilter[]{MappingsLocationFilter.NO_FILTER, MappingsLocationFilter.INTRA_FILE_ONLY}) {
                VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(experiment, mappingsLocationFilter.getFilter(), mappingsTypeFilter1);
                Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
                MetricsCsvWriter.exportToCSV(stats, "rq6-" + experiment.getName() + "-" + mappingsLocationFilter + ".csv", true);
            }
        }
    }
    @Override
    public void run(IExperiment[] exps) throws Exception {
        rq6(exps, mappingsTypeFilter);
    }

    public static void main(String[] args) {
        try {
            new RQ6().run(new IExperiment[]{EExperiment.REF_EXP_3_0, EExperiment.D4J_EXP_3_0});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
