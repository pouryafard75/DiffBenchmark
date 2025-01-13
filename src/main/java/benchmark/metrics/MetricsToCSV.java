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
        IExperiment experiment = ExperimentsEnum.VISITOR_EXP;
        HumanReadableDiffFilter[] filters =  new HumanReadableDiffFilter[] {
                FilterDuringGeneration.NO_FILTER,
                FilterDuringGeneration.INTRA_FILE_ONLY
        };
        for (HumanReadableDiffFilter filter : filters)
        {
            FilterDuringMetricsCalculation noFilter = FilterDuringMetricsCalculation.NO_FILTER;
            VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(
                    experiment,
                    filter,
                    noFilter
            );
            Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
            MetricsCsvWriter.exportToCSV(stats, getCsvFilePath(filter, noFilter, experiment), true, "out/");
        }
    }

    public static String getCsvFilePath(HumanReadableDiffFilter filter, FilterDuringMetricsCalculation filterDuringMetricsCalculation, IExperiment experiment) {
        String delimiter = "-";
        return experiment.getName()
                + delimiter + filter
                + delimiter + filterDuringMetricsCalculation
                + delimiter + experiment.getDataset().getDatasetName()
                + delimiter + experiment.getGenerationStrategy().toString()
                +  ".csv";
    }
}