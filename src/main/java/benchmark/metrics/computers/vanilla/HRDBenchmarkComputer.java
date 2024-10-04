package benchmark.metrics.computers.vanilla;

import benchmark.metrics.computers.DiffMetricsComputer;
import benchmark.metrics.computers.filters.*;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.models.HumanReadableDiff;

import java.io.IOException;
import java.util.*;

/* Created by pourya on 2024-01-27*/
public class HRDBenchmarkComputer {
    private final HumanReadableDiffFilter humanReadableDiffFilter;
    private final CalculationFilter filterDuringMetricsCalculation;

    private final BenchmarkComparisonInput input;

    public HRDBenchmarkComputer(HumanReadableDiffFilter humanReadableDiffFilter, CalculationFilter filterDuringMetricsCalculation, BenchmarkComparisonInput benchmarkComparisonInput) {
        this.humanReadableDiffFilter = humanReadableDiffFilter;
        this.filterDuringMetricsCalculation = filterDuringMetricsCalculation;
        this.input = benchmarkComparisonInput;
    }

    public HRDBenchmarkComputer(BenchmarkComparisonInput benchmarkComparisonInput) {
        this(FilterDuringGeneration.NO_FILTER.getFilter(), FilterDuringMetricsCalculation.NO_FILTER, benchmarkComparisonInput);
    }



    public HumanReadableDiff compute(BaseDiffComparisonResult baseDiffComparisonResult) throws IOException {
        HumanReadableDiff godHRDFinalized = this.humanReadableDiffFilter.make(input.getOriginalGODHRD(), input.getOriginalGODHRD());
        for (Map.Entry<ASTDiffToolEnum, HumanReadableDiff> astDiffToolHumanReadableDiffEntry : input.getRawHRDMap().entrySet()) {
            ASTDiffToolEnum tool = astDiffToolHumanReadableDiffEntry.getKey();
            HumanReadableDiff toolHRD = astDiffToolHumanReadableDiffEntry.getValue();
            HumanReadableDiff toolHRDFinalized = this.humanReadableDiffFilter.make(toolHRD, godHRDFinalized);
            DiffStats diffStats = compareHumanReadableDiffs(godHRDFinalized, toolHRDFinalized, filterDuringMetricsCalculation);
            baseDiffComparisonResult.putStats(tool.name(), diffStats);
        }
        setIgnore(baseDiffComparisonResult);
        return godHRDFinalized;
    }

    public static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, CalculationFilter filterDuringMetricsCalculation) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff, filterDuringMetricsCalculation);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }

    private void setIgnore(BaseDiffComparisonResult baseDiffComparisonResult) {
        HumanReadableDiff diffIgnore = this.humanReadableDiffFilter.make(input.getOriginalTRVHRD(), HumanReadableDiff.makeEmpty());
        baseDiffComparisonResult.setIgnore(FilterUtils.apply(diffIgnore, filterDuringMetricsCalculation));
    }
}

