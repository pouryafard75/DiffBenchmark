package benchmark.metrics;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.util.Collection;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
        IExperiment experiment = ExperimentsEnum.VISITOR_BATTLE;
        HumanReadableDiffFilter[] filters =  new HumanReadableDiffFilter[] {
                FilterDuringGeneration.NO_FILTER,
                FilterDuringGeneration.INTRA_FILE_ONLY
        };
        for (HumanReadableDiffFilter filter : filters)
        {
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(
                    experiment,
                    filter,
                    FilterDuringMetricsCalculation.NO_FILTER
            );
            Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
            MetricsCsvWriter.exportToCSV(stats, getCsvFilePath(filter, experiment), true, "out/");
        }
    }

    private static String getCsvFilePath(HumanReadableDiffFilter filter, IExperiment experiment) {
        return experiment.getOutputFolder() + experiment.getName() + "-" + filter + "-" + experiment.getDataset().getDatasetName() + ".csv";
    }
}