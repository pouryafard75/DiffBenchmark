package benchmark.metrics.computers;

import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static benchmark.utils.PathResolver.repoFolder;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class BenchmarkMetricsComputer {
    private static boolean WRITE_INTERMEDIATE_FILES = false; //TODO: make this consistent
    private final Set<CaseInfo> infos;
    private final Configuration configuration;
//    private final Map<CaseInfo, ProjectASTDiff> rm_projectDiff = new LinkedHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public BenchmarkMetricsComputer(Configuration configuration) throws Exception {
        infos = configuration.getAllCases();
        this.configuration = configuration;
    }

    public List<DiffComparisonResult> generateBenchmarkStats(MappingsLocationFilter mappingsLocationFilter,
                                                             MappingsTypeFilter mappingsTypeFilter) throws IOException {
        List<DiffComparisonResult> benchmarkStats = new ArrayList<>();
        for (CaseInfo info : infos) {
            oneCaseStats(info, benchmarkStats, mappingsLocationFilter, mappingsTypeFilter);
        }
        System.out.println("Finished generating benchmark stats...");
        return benchmarkStats;
    }

    public void oneCaseStats(CaseInfo info, List<DiffComparisonResult> benchmarkStats, MappingsLocationFilter mappingsLocationFilter, MappingsTypeFilter mappingsTypeFilter) throws IOException {
        String folderPath = exportedFolderPathByCaseInfo(info);
        Path dir = Paths.get(configuration.getOutputFolder() + folderPath  + "/");
        System.out.println("Generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        List<Path> paths = getPaths(dir, 1);
        for (Path path : paths) {
            if (path.getFileName().toString().equals("mm")) continue;
            DiffComparisonResult diffComparisonResult = new DiffComparisonResult(info, path.getFileName().toString());
            String godFullPath = path + "/" +  ASTDiffTool.GOD.name() + ".json";
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            HumanReadableDiff godHRD = mapper.readValue(new File(godFullPath), HumanReadableDiff.class);
            populateComparisonResults(diffComparisonResult, godFullPath, godHRD, mappingsLocationFilter, mappingsTypeFilter);
            benchmarkStats.add(diffComparisonResult);
        }
        System.out.println("Finished generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
    }

    private void setIgnoreFromFile(String godFullPath, DiffComparisonResult diffComparisonResult, MappingsLocationFilter mappingsLocationFilter, MappingsTypeFilter mappingsTypeFilter) throws IOException {
        String ignorePath = godFullPath.replace(ASTDiffTool.GOD.name(), ASTDiffTool.TRV.name());
        HumanReadableDiff diffIgnore =  mapper.readValue(new File(ignorePath), HumanReadableDiff.class);
        diffIgnore = mappingsLocationFilter.getFilter().make(diffIgnore);
        diffComparisonResult.setIgnore(mappingsTypeFilter.apply(diffIgnore));
    }

    private void populateComparisonResults(DiffComparisonResult diffComparisonResult, String godFullPath, HumanReadableDiff godHRD, MappingsLocationFilter mappingsLocationFilter, MappingsTypeFilter mappingsTypeFilter) throws IOException {
        HumanReadableDiff godHRDFinalized = mappingsLocationFilter.getFilter().make(godHRD, godHRD);
        setIgnoreFromFile(godFullPath, diffComparisonResult, mappingsLocationFilter, mappingsTypeFilter);
        for (ASTDiffTool tool : configuration.getActiveTools()) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                continue;
            String toolPath = tool.name();
            String toolFullPath = godFullPath.replace(ASTDiffTool.GOD.name(), toolPath);
            HumanReadableDiff toolHRD;
            toolHRD = mapper.readValue(new File(toolFullPath), HumanReadableDiff.class);
            DiffStats diffStats;
            HumanReadableDiff toolHRDFinalized = mappingsLocationFilter.getFilter().make(toolHRD, godHRDFinalized);
            diffStats = compareHumanReadableDiffs(godHRDFinalized, toolHRDFinalized, mappingsLocationFilter, mappingsTypeFilter);
            diffComparisonResult.putStats(toolPath, diffStats);
        }
    }

    private static void writeIntermediateFiles(HumanReadableDiff toolMultiHRD, String godFullPath, String toolName) {
        if (WRITE_INTERMEDIATE_FILES) {
            HumanReadableDiff.write(new File(toolName + ".mm"), toolMultiHRD);
            File newFolder = new File(new File(godFullPath).getParent(), "mm");
            boolean mkdir = newFolder.mkdir();
            HumanReadableDiff.write(
                    new File(newFolder.getAbsolutePath(), toolName + "-mm" + ".csv"),
                    toolMultiHRD);
        }
    }

    public static List<Path> getPaths(Path dir, int exactDepth) throws IOException {
        int minDepth = exactDepth;
        int maxDepth = minDepth + 1;
        return Files.walk(dir, maxDepth)
                .filter(path -> path.toFile().isDirectory())
                .filter(path -> path.getNameCount() - dir.getNameCount() >= minDepth)
                .collect(Collectors.toList());
    }

    private DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, MappingsLocationFilter mappingsLocationFilter, MappingsTypeFilter mappingsTypeFilter) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff, mappingsLocationFilter, mappingsTypeFilter);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }
    public static String exportedFolderPathByCaseInfo(CaseInfo info) {
        return repoFolder(info.getRepo()) +  "/" + info.getCommit();
    }

//    public void writeStatsToCSV(List<DiffComparisonResult> stats, boolean onFly) throws IOException {
//        writeStatsToCSV(stats, this.configuration, this.INCLUDE_INTER_FILE_MAPPINGS, getCsvDestinationFile(), onFly);
//    }
}
