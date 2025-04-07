package benchmark.metrics.computers.vanilla;

import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.metrics.computers.DiffMetricsComputer;
import benchmark.metrics.computers.filters.*;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.models.hrd.AbstractMapping;
import benchmark.models.hrd.HumanReadableDiff;
import benchmark.models.hrd.NecessaryMappings;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/* Created by pourya on 2024-01-27*/
public class HRDBenchmarkComputer {
    private final HumanReadableDiffFilter humanReadableDiffFilter;
    private final CalculationFilter filterDuringMetricsCalculation;

    private final BenchmarkComparisonInput benchmarkComparisonInput;

    public HRDBenchmarkComputer(HumanReadableDiffFilter humanReadableDiffFilter, CalculationFilter filterDuringMetricsCalculation, BenchmarkComparisonInput benchmarkComparisonInput) {
        this.humanReadableDiffFilter = humanReadableDiffFilter;
        this.filterDuringMetricsCalculation = filterDuringMetricsCalculation;
        this.benchmarkComparisonInput = benchmarkComparisonInput;
    }

    public HRDBenchmarkComputer(BenchmarkComparisonInput benchmarkComparisonInput) {
        this(FilterDuringGeneration.NO_FILTER.getFilter(), FilterDuringMetricsCalculation.NO_FILTER, benchmarkComparisonInput);
    }



    public void compute(BaseDiffComparisonResult baseDiffComparisonResult) throws IOException {
        HumanReadableDiff godHRDFinalized = this.humanReadableDiffFilter.make(benchmarkComparisonInput.getOriginalGODHRD(), benchmarkComparisonInput.getOriginalGODHRD());
        for (Map.Entry<ASTDiffToolEnum, HumanReadableDiff> astDiffToolHumanReadableDiffEntry : benchmarkComparisonInput.getRawHRDMap().entrySet()) {
            ASTDiffToolEnum tool = astDiffToolHumanReadableDiffEntry.getKey();
            HumanReadableDiff toolHRD = astDiffToolHumanReadableDiffEntry.getValue();
            HumanReadableDiff toolHRDFinalized = this.humanReadableDiffFilter.make(toolHRD, godHRDFinalized);
            DiffStats diffStats = compareHumanReadableDiffs(godHRDFinalized, toolHRDFinalized, filterDuringMetricsCalculation);
            baseDiffComparisonResult.putStats(tool.name(), diffStats);
        }
        setIgnore(baseDiffComparisonResult);
//        return godHRDFinalized;
    }

    public static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, CalculationFilter filterDuringMetricsCalculation) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff, filterDuringMetricsCalculation);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }

    private void setIgnore(BaseDiffComparisonResult baseDiffComparisonResult) {
        HumanReadableDiff diffIgnore = this.humanReadableDiffFilter.make(benchmarkComparisonInput.getOriginalTRVHRD(), HumanReadableDiff.makeEmpty());
        diffIgnore = FilterUtils.apply(diffIgnore, filterDuringMetricsCalculation);
        HumanReadableDiff prune = prune(diffIgnore);
        baseDiffComparisonResult.setIgnore(prune);
    }

    private HumanReadableDiff prune(HumanReadableDiff diffIgnore) {
        HumanReadableDiff pruned = new HumanReadableDiff();
        pruneWithFunction(NecessaryMappings::getMatchedElements, diffIgnore, pruned);
        pruneWithFunction(NecessaryMappings::getMappings, diffIgnore, pruned);
        return pruned;
    }

    private void pruneWithFunction(Function<NecessaryMappings, Collection<AbstractMapping>> function,
                                   HumanReadableDiff diffIgnore,
                                   HumanReadableDiff pruned) {
        Function<HumanReadableDiff, NecessaryMappings> hrdPart = HumanReadableDiff::getIntraFileMappings;
        for (AbstractMapping mapping : function.apply(hrdPart.apply(diffIgnore))) {
            boolean coveredByAllTools = true; //if all the other tools contain it, then its trivial
            for (HumanReadableDiff toolHRD : benchmarkComparisonInput.getRawHRDMap().values()) {
                if (!function.apply(hrdPart.apply(toolHRD)).contains(mapping)) {
                    coveredByAllTools = false;
                    break;
                }
            }
            if (coveredByAllTools) {
                function.apply(hrdPart.apply(pruned)).add(mapping);
            }
        }
    }
}

