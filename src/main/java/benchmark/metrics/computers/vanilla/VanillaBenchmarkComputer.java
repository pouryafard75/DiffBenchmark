package benchmark.metrics.computers.vanilla;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.BaseBenchmarkComputer;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.FileDiffComparisonResult;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static benchmark.utils.PathResolver.*;


/* Created by pourya on 2023-11-29 2:05 a.m. */
public class VanillaBenchmarkComputer extends BaseBenchmarkComputer {
    private final HumanReadableDiffFilter humanReadableDiffFilter;
    private final MappingsTypeFilter mappingsTypeFilter;
    private boolean onFly = false;

    public void setOnFly(boolean onFly) {
        this.onFly = onFly;
    }

    public VanillaBenchmarkComputer(IExperiment exp,
                                    HumanReadableDiffFilter humanReadableDiffFilter,
                                    MappingsTypeFilter mappingsTypeFilter)
    {
        super(exp);
        this.humanReadableDiffFilter = humanReadableDiffFilter;
        this.mappingsTypeFilter = mappingsTypeFilter;
    }
    public VanillaBenchmarkComputer(IExperiment exp)
    {
        this(exp, MappingsLocationFilter.NO_FILTER.getFilter(), MappingsTypeFilter.NO_FILTER);
    }
    public HumanReadableDiffFilter getHumanReadableDiffFilter() {
        return humanReadableDiffFilter;
    }
    public MappingsTypeFilter getMappingsTypeFilter() {
        return mappingsTypeFilter;
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
//        System.out.println("Generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        List<Path> paths = getPaths(dir, 1);
        for (Path dirPath : paths) {
            BenchmarkComparisonInput read = BenchmarkComparisonInput.read(getExperiment(), info, dirPath.getFileName().toString());
            HRDBenchmarkComputer hrdBenchmarkComputer = new HRDBenchmarkComputer(humanReadableDiffFilter, mappingsTypeFilter, read);
            BaseDiffComparisonResult baseDiffComparisonResult = getBaseDiffComparisonResult(info, dirPath);
            hrdBenchmarkComputer.compute(baseDiffComparisonResult);
            benchmarkStats.add(baseDiffComparisonResult);
        }
        System.out.println("Finished generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        return benchmarkStats;
    }

    private BaseDiffComparisonResult getBaseDiffComparisonResult(IBenchmarkCase info, Path dirPath) throws IOException {
        BaseDiffComparisonResult baseDiffComparisonResult = new FileDiffComparisonResult(info, dirPath.getFileName().toString(), onFly);
        new HRDBenchmarkComputer(getHumanReadableDiffFilter(), getMappingsTypeFilter(),
                BenchmarkComparisonInput.read(this.getExperiment(), info, dirPath.getFileName().toString()))
                .compute(baseDiffComparisonResult);
        return baseDiffComparisonResult;
    }
}
