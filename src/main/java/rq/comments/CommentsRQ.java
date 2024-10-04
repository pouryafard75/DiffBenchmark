package rq.comments;

import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.filters.NoFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import rq.RQ;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;

import static benchmark.data.exp.ExperimentsEnum.REF_EXP_3_0_COMMENTS;

/* Created by pourya on 2024-10-03*/
public class CommentsRQ implements RQ {
    public void run(IExperiment[] experiments) {
        Collection<BaseDiffComparisonResult> stats = new LinkedHashSet<>();

        StringBuilder name = new StringBuilder();
        for (IExperiment experiment : experiments) {
            try {
                stats.addAll(new VanillaBenchmarkComputer
                        (experiment, new NoFilter(), FilterDuringMetricsCalculation.NO_FILTER).compute());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            name.append(experiment.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(stats, "rq1-" + name + ".csv", false);
    }

    public static void main(String[] args) {
        CommentsRQ exp = new CommentsRQ();
        try {
            exp.run(new IExperiment[]{REF_EXP_3_0_COMMENTS});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
