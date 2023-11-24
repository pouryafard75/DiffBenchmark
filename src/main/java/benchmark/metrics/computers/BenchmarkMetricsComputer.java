package benchmark.metrics.computers;

import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.Stats;
import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static benchmark.utils.PathResolver.repoFolder;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class BenchmarkMetricsComputer {
    private static boolean WRITE_INTERMEDIATE_FILES = true;
    private final Set<CaseInfo> infos;
    private final Configuration configuration;
//    private final Map<CaseInfo, ProjectASTDiff> rm_projectDiff = new LinkedHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public BenchmarkMetricsComputer(Configuration configuration) throws Exception {
        infos = configuration.getAllCases();
        this.configuration = configuration;
    }

    public List<DiffComparisonResult> generateBenchmarkStats(MappingsToConsider mappingsToConsider) throws IOException {
        List<DiffComparisonResult> benchmarkStats = new ArrayList<>();
        for (CaseInfo info : infos) {
            oneCaseStats(info, benchmarkStats, configuration, mappingsToConsider);
        }
        System.out.println("Finished generating benchmark stats...");
        return benchmarkStats;
    }

    public static void oneCaseStats(CaseInfo info, List<DiffComparisonResult> benchmarkStats, Configuration configuration, MappingsToConsider mappingsToConsider) throws IOException {
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
            populateComparisonResults(configuration, diffComparisonResult, godFullPath, godHRD, mappingsToConsider);
            benchmarkStats.add(diffComparisonResult);
        }
        System.out.println("Finished generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
    }

    private static void setIgnoreFromFile(String godFullPath, DiffComparisonResult diffComparisonResult) throws IOException {
        String ignorePath = godFullPath.replace(ASTDiffTool.GOD.name(), ASTDiffTool.TRV.name());
        HumanReadableDiff diffIgnore =  mapper.readValue(new File(ignorePath), HumanReadableDiff.class);
        diffComparisonResult.setIgnore(diffIgnore);
    }
    private static void setIgnoreEmpty(String godFullPath, DiffComparisonResult diffComparisonResult) throws IOException {
        diffComparisonResult.setIgnore(HumanReadableDiff.makeEmpty());
    }


    private static void populateComparisonResults(Configuration configuration, DiffComparisonResult diffComparisonResult, String godFullPath, HumanReadableDiff godHRD, MappingsToConsider mappingsToConsider) throws IOException {
        HumanReadableDiff godMultiHRD = null;
        if (mappingsToConsider == MappingsToConsider.MULTI_ONLY) {
            setIgnoreEmpty(godFullPath, diffComparisonResult);
            godMultiHRD = new HumanReadableDiff
                    (new NecessaryMappings(
                            extractMultiMappings(godHRD, godHRD, NecessaryMappings::getMatchedElements),
                            extractMultiMappings(godHRD, godHRD, NecessaryMappings::getMappings)));
            writeIntermediateFiles(godMultiHRD,godFullPath, ASTDiffTool.GOD.name());
        } else {
            setIgnoreFromFile(godFullPath, diffComparisonResult);
        }

        for (ASTDiffTool tool : configuration.getActiveTools()) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                continue;
            String toolPath = tool.name();
            String toolFullPath = godFullPath.replace(ASTDiffTool.GOD.name(), toolPath);
            HumanReadableDiff toolHRD;
            toolHRD = mapper.readValue(new File(toolFullPath), HumanReadableDiff.class);
            DiffStats diffStats;
            if (mappingsToConsider == MappingsToConsider.MULTI_ONLY) {
                HumanReadableDiff toolMultiHRD = new HumanReadableDiff
                        (new NecessaryMappings(
                                extractMultiMappings(toolHRD, godMultiHRD, NecessaryMappings::getMatchedElements),
                                extractMultiMappings(toolHRD, godMultiHRD, NecessaryMappings::getMappings)));
                writeIntermediateFiles(toolMultiHRD, godFullPath, tool.name());
                diffStats = compareHumanReadableDiffs(godMultiHRD, toolMultiHRD, mappingsToConsider);
            }
            else {
                diffStats = compareHumanReadableDiffs(godHRD, toolHRD, mappingsToConsider);
            }
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

    private static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, MappingsToConsider mappingsToConsider) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff, mappingsToConsider);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }


    private static Set<AbstractMapping> extractMultiMappings(HumanReadableDiff godDiff, HumanReadableDiff other,
                                                             Function<NecessaryMappings, Collection<AbstractMapping>> function) {
        Set<AbstractMapping> multi = new HashSet<>();
        for (AbstractMapping m1 : function.apply(godDiff.intraFileMappings)) {
            for (AbstractMapping m2 : function.apply(other.intraFileMappings)) {
                if (m1.isMultiMappingPartner(m2)) {
                    multi.add(m1);
                    break;
                }
            }
        }
        return multi;
    }

    public static String exportedFolderPathByCaseInfo(CaseInfo info) {
        return repoFolder(info.getRepo()) +  "/" + info.getCommit();
    }

//    public void writeStatsToCSV(List<DiffComparisonResult> stats, boolean onFly) throws IOException {
//        writeStatsToCSV(stats, this.configuration, this.INCLUDE_INTER_FILE_MAPPINGS, getCsvDestinationFile(), onFly);
//    }
}
