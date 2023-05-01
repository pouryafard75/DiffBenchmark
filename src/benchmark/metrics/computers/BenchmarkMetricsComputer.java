package benchmark.metrics.computers;

import benchmark.utils.CaseInfo;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.models.DiffIgnore;
import benchmark.metrics.models.DiffStats;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.models.ASTDiff;
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
import static benchmark.utils.PathResolver.replaceFileName;
import static benchmark.utils.PathResolver.repoFolder;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class BenchmarkMetricsComputer {

    private final List<CaseInfo> infos;
    private Map<CaseInfo, Set<ASTDiff>> diffs = new LinkedHashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();

    public BenchmarkMetricsComputer(List<CaseInfo> info) throws Exception {
        this.infos = info;
        runRMLocally();
    }
    public String[] getActiveTools(){
        return toolPathMap.keySet().toArray(new String[0]);
    }

    private void runRMLocally() throws Exception {
        for (CaseInfo info : infos) {
            GitService gitService = new GitServiceImpl();String repoFolder = info.getRepo().substring(info.getRepo().lastIndexOf("/"), info.getRepo().indexOf(".git"));
            Repository repo = gitService.cloneIfNotExists(REPOS + repoFolder, info.getRepo());
            Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, info.getCommit());
            diffs.put(info, astDiffs);
        }
    }
    public List<DiffComparisonResult> generateBenchmarkStats() throws IOException {

        List<DiffComparisonResult> benchmarkStats = new ArrayList<>();
        for (CaseInfo info : infos) {
            String folderPath = exportedFolderPathByCaseInfo(info);
            String godFullFolderPath = GOD_PATH + folderPath;
            Path dir = Paths.get(godFullFolderPath);
            Files.walk(dir).filter(path -> path.toFile().isFile()).forEach(path ->
                    {
                        DiffComparisonResult diffComparisonResult = new DiffComparisonResult(info, path.getFileName().toString());
                        String godFullPath = path.toString();
                        HumanReadableDiff godHRD;

                        Set<ASTDiff> astDiffs = diffs.get(info);
                        List<ASTDiff> collect = astDiffs.stream().filter(astDiff -> replaceFileName(astDiff.getSrcPath()).equals(path.getFileName().toString())).collect(Collectors.toList());
                        ASTDiff astDiff = null;
                        if (collect.size() > 0)
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
                            String toolFullPath = godFullPath.replace(GOD_PATH, toolDir);
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

    public static void writeStatsToCSV(List<DiffComparisonResult> stats, String[] activeTools) throws IOException {
        FileWriter writer = new FileWriter(STATS_CSV);
        String[] toolNames = activeTools;
        writer.append("url,srcFileName,ignoredMappings,ignoredElements,");
        int index = 0;
        for (String toolName : toolNames) {
            index += 1;
            toolName = "";
            writer.append(toolName).append("TP_raw,").append(toolName).append("TP,").append(toolName).append("FP,").append(toolName).append("FN,");
            writer.append(toolName).append("TP_raw,").append(toolName).append("TP,").append(toolName).append("FP,").append(toolName).append("FN");
            if (index != toolNames.length)
                writer.append(",");
//            writer.append(toolName).append("_ELEMENTS_TP_ORIGINAL,").append(toolName).append("_ELEMENTS_TP_REFINED,").append(toolName).append("_ELEMENTS_FP,").append(toolName).append("_ELEMENTS_FN,");
        }
        writer.append("\n");
        for (DiffComparisonResult stat : stats) {
            writer.append(stat.getCaseInfo().makeURL());
            writer.append(",");
            writer.append(stat.getSrcFileName());
            writer.append(",");
            writer.append(String.valueOf(stat.getIgnore().getNumberOfIgnoredMappings()));
            writer.append(",");
            writer.append(String.valueOf(stat.getIgnore().getNumberOfIgnoredElements()));
            writer.append(",");
            index = 0;
            for (String toolName : toolNames) {
                index ++;
                DiffStats tool = stat.getDiffStatsList().get(toolName);
                writer.append(String.valueOf(tool.getAbstractMappingStats().getTP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getAbstractMappingStats().getTP() - stat.getIgnore().getNumberOfIgnoredMappings()));
                writer.append(",");
                writer.append(String.valueOf(tool.getAbstractMappingStats().getFP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getAbstractMappingStats().getFN()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getTP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getTP() - stat.getIgnore().getNumberOfIgnoredElements()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getFP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getFN()));
                if (index != toolNames.length)
                    writer.append(",");
            }
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }
}
