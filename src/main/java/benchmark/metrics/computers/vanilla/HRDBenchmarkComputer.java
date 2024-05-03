package benchmark.metrics.computers.vanilla;

import benchmark.metrics.computers.DiffMetricsComputer;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.models.HumanReadableDiff;

import java.io.IOException;
import java.util.*;

/* Created by pourya on 2024-01-27*/
public class HRDBenchmarkComputer {
    private final HumanReadableDiffFilter humanReadableDiffFilter;
    private final MappingsTypeFilter mappingsTypeFilter;

    private final BenchmarkComparisonInput input;

    public HRDBenchmarkComputer(HumanReadableDiffFilter humanReadableDiffFilter, MappingsTypeFilter mappingsTypeFilter, BenchmarkComparisonInput benchmarkComparisonInput) {
        this.humanReadableDiffFilter = humanReadableDiffFilter;
        this.mappingsTypeFilter = mappingsTypeFilter;
        this.input = benchmarkComparisonInput;
    }

    public HRDBenchmarkComputer(BenchmarkComparisonInput benchmarkComparisonInput) {
        this(MappingsLocationFilter.NO_FILTER.getFilter(), MappingsTypeFilter.NO_FILTER, benchmarkComparisonInput);
    }



    public HumanReadableDiff compute(BaseDiffComparisonResult baseDiffComparisonResult) throws IOException {
        HumanReadableDiff godHRDFinalized = this.humanReadableDiffFilter.make(input.getOriginalGODHRD(), input.getOriginalGODHRD());
        for (Map.Entry<ASTDiffTool, HumanReadableDiff> astDiffToolHumanReadableDiffEntry : input.getRawHRDMap().entrySet()) {
            ASTDiffTool tool = astDiffToolHumanReadableDiffEntry.getKey();
            HumanReadableDiff toolHRD = astDiffToolHumanReadableDiffEntry.getValue();
            HumanReadableDiff toolHRDFinalized = this.humanReadableDiffFilter.make(toolHRD, godHRDFinalized);
            DiffStats diffStats = compareHumanReadableDiffs(godHRDFinalized, toolHRDFinalized, mappingsTypeFilter);
            baseDiffComparisonResult.putStats(tool.name(), diffStats);
        }
        setIgnore(baseDiffComparisonResult);
        return godHRDFinalized;
    }

    public static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, MappingsTypeFilter mappingsTypeFilter) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff, mappingsTypeFilter);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }

    private void setIgnore(BaseDiffComparisonResult baseDiffComparisonResult) {
        HumanReadableDiff diffIgnore = this.humanReadableDiffFilter.make(input.getOriginalTRVHRD(), HumanReadableDiff.makeEmpty());
        baseDiffComparisonResult.setIgnore(mappingsTypeFilter.apply(diffIgnore));
    }
}

