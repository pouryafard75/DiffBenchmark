package rq.adb;

import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.util.Collection;
import java.util.Set;

import static benchmark.metrics.MetricsToCSV.getCsvFilePath;

/* Created by pourya on 2025-01-12*/
public record BaseRQDriverRoutine(HumanReadableDiffFilter[] filters, Set<IASTDiffTool> guards) implements DiffBenchmarkRQ {

    public static final FilterDuringMetricsCalculation filterDuringMetricsCalculation = FilterDuringMetricsCalculation.NO_FILTER;
    public static final String folder = "csv-outputs/adb-paper/";

    @Override
    public void run(IExperiment[] confs) throws Exception {
        for (IExperiment experiment : confs) {
            for (HumanReadableDiffFilter filter : filters) {
                VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(
                        experiment,
                        filter,
                        filterDuringMetricsCalculation
                );
                computer.setDiffIgnoreGuards(guards);
                Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
                MetricsCsvWriter.exportToCSV(stats, getCsvFilePath(filter, filterDuringMetricsCalculation, experiment), true, folder);
            }
        }
    }
}
