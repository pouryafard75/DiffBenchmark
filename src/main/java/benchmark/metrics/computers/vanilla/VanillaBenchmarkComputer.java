package benchmark.metrics.computers.vanilla;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.metrics.computers.BaseBenchmarkComputer;
import benchmark.metrics.computers.filters.CalculationFilter;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.FileDiffComparisonResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static benchmark.utils.PathResolver.*;


/* Created by pourya on 2023-11-29 2:05 a.m. */
public class VanillaBenchmarkComputer extends BaseBenchmarkComputer {
    private final HumanReadableDiffFilter humanReadableDiffFilter;
    private final FilterDuringMetricsCalculation filterDuringMetricsCalculation;
    private boolean onFly = false;

    public Set<IASTDiffTool> diffIgnoreGuards = Set.of();

    public void setDiffIgnoreGuards(Set<IASTDiffTool> diffIgnoreGuards) {
        this.diffIgnoreGuards = diffIgnoreGuards;
    }

    public void setOnFly(boolean onFly) {
        this.onFly = onFly;
    }

    public VanillaBenchmarkComputer(IExperiment exp,
                                    HumanReadableDiffFilter humanReadableDiffFilter,
                                    FilterDuringMetricsCalculation filterDuringMetricsCalculation)
    {
        super(exp);
        this.humanReadableDiffFilter = humanReadableDiffFilter;
        this.filterDuringMetricsCalculation = filterDuringMetricsCalculation;
    }
    public VanillaBenchmarkComputer(IExperiment exp)
    {
        this(exp, FilterDuringGeneration.NO_FILTER.getFilter(), FilterDuringMetricsCalculation.NO_FILTER);
    }
    public HumanReadableDiffFilter getHumanReadableDiffFilter() {
        return humanReadableDiffFilter;
    }
    public CalculationFilter getCalculationFilter() {
        return filterDuringMetricsCalculation;
    }
    public Collection<? extends BaseDiffComparisonResult> compute() throws IOException {
        Collection<BaseDiffComparisonResult> stats = new ArrayList<>();
        for (IBenchmarkCase info : getExperiment().getDataset().getCases()) {
            stats.addAll(compute(info));
        }
        return stats;
    }

    @Override
    public Collection<? extends BaseDiffComparisonResult> compute(IBenchmarkCase info) throws IOException {
        Collection<BaseDiffComparisonResult> benchmarkStats = new ArrayList<>();
        String folderPath = exportedFolderPathByCaseInfo(info);
        Path dir = Paths.get(getExperiment().getOutputFolder() + folderPath  + "/");
        System.out.println("Generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        List<Path> paths = getPaths(dir, 1);
        for (Path dirPath : paths) {
//            BenchmarkComparisonInput read = BenchmarkComparisonInput.read(getExperiment(), info, dirPath.getFileName().toString());
//            HRDBenchmarkComputer hrdBenchmarkComputer = new HRDBenchmarkComputer(humanReadableDiffFilter, filterDuringMetricsCalculation, read);
            BaseDiffComparisonResult baseDiffComparisonResult = getBaseDiffComparisonResult(info, dirPath);
//            hrdBenchmarkComputer.compute(baseDiffComparisonResult);
            benchmarkStats.add(baseDiffComparisonResult);
        }
        System.out.println("Finished generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        polishStats(benchmarkStats, getExperiment());
        return benchmarkStats;
    }

    private void polishStats(Collection<BaseDiffComparisonResult> benchmarkStats, IExperiment experiment) {
        // Get the ordered list of tool short names
        List<String> orderedToolNames = experiment.getTools().stream()
                .map(IASTDiffTool::getShortName)
                .toList();
        for (BaseDiffComparisonResult benchmarkStat : benchmarkStats) {
            Map<String, DiffStats> originalMap = benchmarkStat.getDiffStatsList();
            Map<String, DiffStats> orderedMap = new LinkedHashMap<>();

            for (String name : orderedToolNames) {
                if (originalMap.containsKey(name)) {
                    orderedMap.put(name, originalMap.get(name));
                }
            }
            // Clear and update the map in place, preserving order
            originalMap.clear();
            originalMap.putAll(orderedMap);
        }
    }

    private BaseDiffComparisonResult getBaseDiffComparisonResult(IBenchmarkCase info, Path dirPath) throws IOException {
        BaseDiffComparisonResult baseDiffComparisonResult = new FileDiffComparisonResult(info, dirPath.getFileName().toString(), onFly);
        new HRDBenchmarkComputer(getHumanReadableDiffFilter(), getCalculationFilter(),
                BenchmarkComparisonInput.read(this.getExperiment(), info, dirPath.getFileName().toString(), diffIgnoreGuards))
                .compute(baseDiffComparisonResult, diffIgnoreGuards);
        return baseDiffComparisonResult;
    }
}
