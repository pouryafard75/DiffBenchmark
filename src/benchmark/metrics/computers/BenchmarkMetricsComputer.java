package benchmark.metrics.computers;

import benchmark.metrics.models.Stats;
import benchmark.utils.CaseInfo;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.models.DiffIgnore;
import benchmark.metrics.models.DiffStats;
import benchmark.utils.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import benchmark.oracle.models.HumanReadableDiff;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static benchmark.utils.Configuration.*;
import static benchmark.utils.Helpers.runWhatever;
import static benchmark.utils.PathResolver.*;
import static benchmark.utils.PathResolver.getAfterDir;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class BenchmarkMetricsComputer {

    private final static boolean INCLUDE_INTER_FILE_MAPPINGS = true;
    private final Set<CaseInfo> infos;
    private final Configuration configuration;
    private final Map<CaseInfo, Set<ASTDiff>> rm_astdiffs = new LinkedHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();


    public BenchmarkMetricsComputer(Configuration configuration) throws Exception {
        infos = configuration.getAllCases();
        this.configuration = configuration;
        runRMLocally();
    }

    private void runRMLocally() throws Exception {
        for (CaseInfo info : infos)
            rm_astdiffs.put(
                    info,
                    runWhatever(info.getRepo(), info.getCommit()).getDiffSet()
            );
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
                String godFullPath = path + "/" +  GOD + ".json";
                Set<ASTDiff> astDiffs = rm_astdiffs.get(info);
                List<ASTDiff> collected = astDiffs.stream()
                        .filter(astDiff -> fileNameAsFolder(astDiff.getSrcPath()).equals(path.getFileName().toString()))
                        .collect(Collectors.toList());
                if (collected.size() != 1 || collected.get(0) == null)
                    throw new RuntimeException("ASTDiff is null!");
                ASTDiff astDiff = collected.get(0);
                HumanReadableDiff godHRD = mapper.readValue(new File(godFullPath), HumanReadableDiff.class);
                populateComparisonResult(diffComparisonResult, godFullPath, godHRD);
                DiffIgnore diffIgnore = new DiffIgnore(astDiff.src.getRoot(), astDiff.dst.getRoot());
                diffComparisonResult.setIgnore(diffIgnore);
                benchmarkStats.add(diffComparisonResult);
            }
            System.out.println("Finished generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
        }
        System.out.println("Finished generating benchmark stats...");
        return benchmarkStats;
    }

    private static void populateComparisonResult(DiffComparisonResult diffComparisonResult, String godFullPath, HumanReadableDiff godHRD) throws IOException {
        for (String tool : activeTools) {
            String toolName = tool;
            String toolPath = tool;
            String toolFullPath = godFullPath.replace(GOD, toolPath);
            HumanReadableDiff toolHRD;
            toolHRD = mapper.readValue(new File(toolFullPath), HumanReadableDiff.class);
            DiffStats diffStats = compareHumanReadableDiffs(godHRD, toolHRD);
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

    private static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff, INCLUDE_INTER_FILE_MAPPINGS);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }

    private static String exportedFolderPathByCaseInfo(CaseInfo info) {
        return repoFolder(info.getRepo()) +  "/" + info.getCommit();
    }

    public void writeStatsToCSV(List<DiffComparisonResult> stats) throws IOException {
        String[] activeTools = Configuration.getActiveTools();
        try (FileWriter writer = new FileWriter(configuration.getOutputFolder() + configuration.getCsvDestinationFile())) {
            writeHeader(writer, activeTools);
            writeData(writer, stats, activeTools);
        }
    }

    private static void writeHeader(FileWriter writer, String[] activeTools) throws IOException {
        StringBuilder header = new StringBuilder("url,srcFileName,ignoredMappings,ignoredElements,");
        for (String toolName : activeTools) {
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

    private static void writeData(FileWriter writer, List<DiffComparisonResult> stats, String[] activeTools) throws IOException {
        for (DiffComparisonResult stat : stats) {
            StringBuilder row = new StringBuilder();
            row.append(stat.getCaseInfo().makeURL()).append(",")
                    .append(stat.getSrcFileName()).append(",")
                    .append(stat.getIgnore().getNumberOfIgnoredMappings()).append(",")
                    .append(stat.getIgnore().getNumberOfIgnoredElements()).append(",");

            for (String toolName : activeTools) {
                DiffStats tool = stat.getDiffStatsList().get(toolName);
                appendStats(row, tool.getAbstractMappingStats(), stat.getIgnore().getNumberOfIgnoredMappings());
                appendStats(row, tool.getProgramElementStats(), stat.getIgnore().getNumberOfIgnoredElements());
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
