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
import static benchmark.utils.PathResolver.*;
import static benchmark.utils.PathResolver.getAfterDir;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class BenchmarkMetricsComputer {

    private final Set<CaseInfo> infos;
    private final Configuration configuration;
    private Map<CaseInfo, Set<ASTDiff>> diffs = new LinkedHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();
    private ProjectASTDiff projectASTDiff;


    public BenchmarkMetricsComputer(Configuration configuration) throws Exception {
        infos = configuration.allCases;
        this.configuration = configuration;
        runRMLocally();
    }
    public String[] getActiveTools(){
        return toolPathMap.keySet().toArray(new String[0]);
    }

    private void runRMLocally() throws Exception {
        for (CaseInfo info : infos) {
            String repo = info.getRepo();
            String commit = info.getCommit();
            Set<ASTDiff> astDiffs;
            if (repo.contains("github")) {
                GitService gitService = new GitServiceImpl();
                String repoFolder = repo.substring(repo.lastIndexOf("/"), repo.indexOf(".git"));
                Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, repo);
//                astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
                projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
            }
            else{
                String projectDir = repo;
                String bugID = commit;
                projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtDirectories(
                        Path.of(getBeforeDir(projectDir, bugID)), Path.of(getAfterDir(projectDir, bugID)));
            }
            diffs.put(info, projectASTDiff.getDiffSet());
        }
    }
    public List<DiffComparisonResult> generateBenchmarkStats() throws IOException {
        List<DiffComparisonResult> benchmarkStats = new ArrayList<>();
        for (CaseInfo info : infos) {
            String folderPath = exportedFolderPathByCaseInfo(info);
            Path dir = Paths.get(configuration.output_folder + folderPath  + "/");
//            Files.walk(dir).filter(path -> path.toFile().isFile()).forEach(path ->
            final int minDepth = 1;
            final int maxDepth = minDepth + 1;
            Files.walk(dir,maxDepth)
                    .filter(path-> path.toFile().isDirectory())
                    .filter(path -> path.getNameCount() - dir.getNameCount() >= minDepth)
                    .forEach( path ->
                    {
                        DiffComparisonResult diffComparisonResult = new DiffComparisonResult(info, path.getFileName().toString());
                        String godFullPath = path + "/" +  GOD + ".json";
                        HumanReadableDiff godHRD;
//
                        Set<ASTDiff> astDiffs = diffs.get(info);
                        List<ASTDiff> collect = astDiffs.stream().filter
                                (astDiff -> fileNameAsFolder(astDiff.getSrcPath()).equals(path.getFileName().toString())).collect(Collectors.toList());
                        ASTDiff astDiff = null;
                        if (collect.size() == 1)
                            astDiff = collect.get(0);
                        if (astDiff == null)
                            throw new RuntimeException("ASTDiff is null!");
                        try {
                            godHRD = mapper.readValue(new File(godFullPath), HumanReadableDiff.class);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        for (Map.Entry<String, String> entry : toolPathMap.entrySet()) {
                            String toolDir = entry.getValue();
                            String toolFullPath = godFullPath.replace(GOD, toolDir);
                            HumanReadableDiff toolHRD;
                            try {
                                toolHRD = mapper.readValue(new File(toolFullPath), HumanReadableDiff.class);
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            DiffStats diffStats = compareHumanReadableDiffs(godHRD, toolHRD);
                            diffComparisonResult.putStats(entry.getKey(), diffStats);
                        }
                        DiffIgnore diffIgnore = new DiffIgnore(astDiff.src.getRoot(), astDiff.dst.getRoot(), astDiff.getAllMappings());
                        diffComparisonResult.setIgnore(diffIgnore);
                        benchmarkStats.add(diffComparisonResult);
                    });
        }
        return benchmarkStats;
    }

    private static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff) {
        DiffMetricsComputer diffMetricsComputer = new DiffMetricsComputer(godDiff, toolDiff);
        return new DiffStats(diffMetricsComputer.programElementStats(), diffMetricsComputer.mappingStats());
    }

    private static String exportedFolderPathByCaseInfo(CaseInfo info) {
        return repoFolder(info.getRepo()) +  "/" + info.getCommit();
    }

    public static void writeStatsToCSV2(List<DiffComparisonResult> stats, String[] activeTools) throws IOException {
        try (FileWriter writer = new FileWriter(STATS_CSV)) {
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
