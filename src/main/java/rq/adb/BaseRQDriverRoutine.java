package rq.adb;

import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static benchmark.metrics.MetricsToCSV.getCsvFilePath;

/* Created by pourya on 2025-01-12*/
public record BaseRQDriverRoutine(HumanReadableDiffFilter[] filters) implements DiffBenchmarkRQ {

    public static final FilterDuringMetricsCalculation filterDuringMetricsCalculation = FilterDuringMetricsCalculation.NO_FILTER;
    public static final String folder = "csv-outputs/adb-paper/";

    @Override
    public Map<IExperiment, Collection<? extends BaseDiffComparisonResult>> calculate(IExperiment[] confs) throws Exception {
        Map<IExperiment, Collection<? extends BaseDiffComparisonResult>> results = new LinkedHashMap<>();
        for (IExperiment experiment : confs) {

            for (HumanReadableDiffFilter filter : filters) {
                VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(
                        experiment,
                        filter,
                        filterDuringMetricsCalculation
                );
                Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
                results.put(experiment, stats);
                MetricsCsvWriter.exportToCSV(stats, getCsvFilePath(filter, filterDuringMetricsCalculation, experiment), true, folder);
            }
        }
        return results;
    }
}
