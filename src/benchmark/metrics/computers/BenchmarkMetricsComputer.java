package benchmark.metrics.computers;

import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.Stats;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static benchmark.utils.PathResolver.repoFolder;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class BenchmarkMetricsComputer {
    private final boolean INCLUDE_INTER_FILE_MAPPINGS;
    private final Set<CaseInfo> infos;
    private final Configuration configuration;
//    private final Map<CaseInfo, ProjectASTDiff> rm_projectDiff = new LinkedHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();


    public BenchmarkMetricsComputer(Configuration configuration, boolean includeInterFileMappings) throws Exception {
        infos = configuration.getAllCases();
        this.configuration = configuration;
        INCLUDE_INTER_FILE_MAPPINGS = includeInterFileMappings;
    }
    public List<DiffComparisonResult> generateBenchmarkStats() throws IOException {
        List<DiffComparisonResult> benchmarkStats = new ArrayList<>();
        for (CaseInfo info : infos) {
            String folderPath = exportedFolderPathByCaseInfo(info);
            Path dir = Paths.get(configuration.getOutputFolder() + folderPath  + "/");
            System.out.println("Generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
            List<Path> paths = getPaths(dir, 1);
            for (Path path : paths) {
                DiffComparisonResult diffComparisonResult = new DiffComparisonResult(info, path.getFileName().toString());
                String godFullPath = path + "/" +  ASTDiffTool.GOD.name() + ".json";
                mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                HumanReadableDiff godHRD = mapper.readValue(new File(godFullPath), HumanReadableDiff.class);
                populateComparisonResult(diffComparisonResult, godFullPath, godHRD);
                String ignorePath = godFullPath.replace(ASTDiffTool.GOD.name(), ASTDiffTool.TRV.name());
                HumanReadableDiff diffIgnore =  mapper.readValue(new File(ignorePath), HumanReadableDiff.class);
                diffComparisonResult.setIgnore(diffIgnore);
                benchmarkStats.add(diffComparisonResult);
            }
            System.out.println("Finished generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        }
        System.out.println("Finished generating benchmark stats...");
        return benchmarkStats;
    }

    private void populateComparisonResult(DiffComparisonResult diffComparisonResult, String godFullPath, HumanReadableDiff godHRD) throws IOException {
        for (ASTDiffTool tool : this.configuration.getActiveTools()) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                continue;
            String toolPath = tool.name();
            String toolFullPath = godFullPath.replace(ASTDiffTool.GOD.name(), toolPath);
            HumanReadableDiff toolHRD;
            toolHRD = mapper.readValue(new File(toolFullPath), HumanReadableDiff.class);
            DiffStats diffStats = compareHumanReadableDiffs(godHRD, toolHRD, INCLUDE_INTER_FILE_MAPPINGS);
            diffComparisonResult.putStats(toolPath, diffStats);
        }
    }

    private static List<Path> getPaths(Path dir, int exactDepth) throws IOException {
        int minDepth = exactDepth;
        int maxDepth = minDepth + 1;
        return Files.walk(dir, maxDepth)
                .filter(path -> path.toFile().isDirectory())
                .filter(path -> path.getNameCount() - dir.getNameCount() >= minDepth)
                .collect(Collectors.toList());
    }

    private static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, boolean INCLUDE_INTER_FILE_MAPPINGS) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff, INCLUDE_INTER_FILE_MAPPINGS);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }

    private static String exportedFolderPathByCaseInfo(CaseInfo info) {
        return repoFolder(info.getRepo()) +  "/" + info.getCommit();
    }

    public void writeStatsToCSV(List<DiffComparisonResult> stats) throws IOException {
        ASTDiffTool[] activeTools = configuration.getActiveTools();
        try (FileWriter writer = new FileWriter(configuration.getOutputFolder() +
                (this.INCLUDE_INTER_FILE_MAPPINGS
                ? "withInterFileMappings-"
                : "withoutInterFileMappings-") +
                configuration.getCsvDestinationFile())) {
            writeHeader(writer, activeTools);
            writeData(writer, stats, activeTools);
        }
    }

    private static void writeHeader(FileWriter writer, ASTDiffTool[] activeTools) throws IOException {
        StringBuilder header = new StringBuilder("url,srcFileName,ignoredMappings,ignoredElements,");
        for (ASTDiffTool tool : activeTools) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                continue;
            String toolName = tool.name();
            for (String criteria : new String[]{"abstractMapping", "programElement"}) {
                String prefixedToolName = toolName + "_";
                header
                    .append(prefixedToolName).append("TP_raw_").append(criteria).append(",")
                    .append(prefixedToolName).append("TP_").append(criteria).append(",")
                    .append(prefixedToolName).append("FP_").append(criteria).append(",")
                    .append(prefixedToolName).append("FN_").append(criteria).append(",");
            }
        }
        header.deleteCharAt(header.length() - 1); // Remove trailing comma
        header.append("\n");
        writer.append(header.toString());
    }

    private static void writeData(FileWriter writer, List<DiffComparisonResult> stats, ASTDiffTool[] activeTools) throws IOException {
        for (DiffComparisonResult stat : stats) {
            StringBuilder row = new StringBuilder();
            row.append(stat.getCaseInfo().makeURL()).append(",")
                    .append(stat.getSrcFileName()).append(",")
                    .append(stat.getIgnore().intraFileMappings.getMappings().size()).append(",")
                    .append(stat.getIgnore().intraFileMappings.getMatchedElements().size()).append(",");

            for (ASTDiffTool tool : activeTools) {
                if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                    continue;
                String toolName = tool.name();
                DiffStats toolStat = stat.getDiffStatsList().get(toolName);
                appendStats(row, toolStat.getAbstractMappingStats(), stat.getIgnore().intraFileMappings.getMappings().size());
                appendStats(row, toolStat.getProgramElementStats(), stat.getIgnore().intraFileMappings.getMatchedElements().size());
            }
            row.deleteCharAt(row.length() - 1); // Remove trailing comma
            row.append("\n");
            writer.append(row.toString());
        }
    }

    private static void appendStats(StringBuilder row, Stats stats, int ignore) {
        row
        .append(stats.getTP()).append(",")
        .append(stats.getTP() - ignore).append(",")
        .append(stats.getFP()).append(",")
        .append(stats.getFN()).append(",");
    }
}
