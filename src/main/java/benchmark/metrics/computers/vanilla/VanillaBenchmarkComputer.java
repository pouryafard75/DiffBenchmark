package benchmark.metrics.computers.vanilla;

import benchmark.metrics.computers.BaseBenchmarkComputer;
import benchmark.metrics.computers.filters.HumanReadableDiffFilter;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.computers.refactoring.RefactoringWiseBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.FileDiffComparisonResult;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static benchmark.utils.PathResolver.*;


/* Created by pourya on 2023-11-29 2:05 a.m. */
public class VanillaBenchmarkComputer extends BaseBenchmarkComputer {
    private final HumanReadableDiffFilter humanReadableDiffFilter;
    private final MappingsTypeFilter mappingsTypeFilter;

    public VanillaBenchmarkComputer(Configuration configuration,
                                    HumanReadableDiffFilter humanReadableDiffFilter,
                                    MappingsTypeFilter mappingsTypeFilter)
    {
        super(configuration);
        this.humanReadableDiffFilter = humanReadableDiffFilter;
        this.mappingsTypeFilter = mappingsTypeFilter;
    }
    public VanillaBenchmarkComputer(Configuration configuration)
    {
        this(configuration, MappingsLocationFilter.NO_FILTER.getFilter(), MappingsTypeFilter.NO_FILTER);
    }
    public HumanReadableDiffFilter getHumanReadableDiffFilter() {
        return humanReadableDiffFilter;
    }
    public MappingsTypeFilter getMappingsTypeFilter() {
        return mappingsTypeFilter;
    }
    public Collection<? extends BaseDiffComparisonResult> compute() throws IOException {
        Collection<BaseDiffComparisonResult> stats = new ArrayList<>();
        for (CaseInfo info : getConfiguration().getAllCases()) {
            stats.addAll(compute(info));
        }
        return stats;
    }

    @Override
    public Collection<? extends BaseDiffComparisonResult> compute(CaseInfo info) throws IOException {
        Collection<BaseDiffComparisonResult> benchmarkStats = new ArrayList<>();
        String folderPath = exportedFolderPathByCaseInfo(info);
        Path dir = Paths.get(getConfiguration().getOutputFolder() + folderPath  + "/");
        System.out.println("Generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        List<Path> paths = getPaths(dir, 1);
        for (Path dirPath : paths) {
            BaseDiffComparisonResult baseDiffComparisonResult = new FileDiffComparisonResult(info, dirPath.getFileName().toString());
            populateComparisonResults(baseDiffComparisonResult, dirPath, getHumanReadableDiffFilter());
            benchmarkStats.add(baseDiffComparisonResult);
        }
        System.out.println("Finished generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        return benchmarkStats;
    }

    /**
     * @return Finalized HRD of the god tool after all the filtration. Originally, this method was supposed to be void.
     * <h> However based on my later experiments, I found out that the godFinalizedHRD can be beneficial in some cases such as {@link RefactoringWiseBenchmarkComputer}. So, I decided to return the god tool's HumanReadableDiff </h>
     */
    protected HumanReadableDiff populateComparisonResults(BaseDiffComparisonResult baseDiffComparisonResult, Path dirPath, HumanReadableDiffFilter filter) throws IOException {
        String godFullPath = dirPath.resolve(ASTDiffTool.GOD.name() + ".json").toString();
        HumanReadableDiff originalGodHRD = getMapper().readValue(new File(godFullPath), HumanReadableDiff.class);
        HumanReadableDiff godHRDFinalized = filter.make(originalGodHRD, originalGodHRD);
        for (ASTDiffTool tool : getConfiguration().getActiveTools()) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                continue;
            String toolPath = tool.name();
            String toolFullPath = godFullPath.replace(ASTDiffTool.GOD.name(), toolPath);
            HumanReadableDiff toolHRD;
            toolHRD = getMapper().readValue(new File(toolFullPath), HumanReadableDiff.class);
            DiffStats diffStats;
            HumanReadableDiff toolHRDFinalized = filter.make(toolHRD, godHRDFinalized);
            diffStats = compareHumanReadableDiffs(godHRDFinalized, toolHRDFinalized, mappingsTypeFilter);
            baseDiffComparisonResult.putStats(toolPath, diffStats);
        }
        setIgnore(godFullPath, baseDiffComparisonResult, filter);
        return godHRDFinalized;
    }

    protected void setIgnore(String godFullPath, BaseDiffComparisonResult baseDiffComparisonResult, HumanReadableDiffFilter filter) throws IOException {
        String ignorePath = godFullPath.replace(ASTDiffTool.GOD.name(), ASTDiffTool.TRV.name());
        HumanReadableDiff diffIgnore =  getMapper().readValue(new File(ignorePath), HumanReadableDiff.class);
        diffIgnore = filter.make(diffIgnore, HumanReadableDiff.makeEmpty());
        baseDiffComparisonResult.setIgnore(mappingsTypeFilter.apply(diffIgnore));
    }
}
