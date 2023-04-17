package benchmark.Oracle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static benchmark.Oracle.OracleGenerator.replaceFileName;
import static benchmark.Oracle.OracleGenerator.repoFolder;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class BenchmarkMetrics {
    private static final String GOD_PATH = "output/GOD/";
    private static final String RMD_PATH =  "output/RMD/";
    private static final String GTG_PATH = "output/GTG/";
    private static final String GTS_PATH = "output/GTS/";
    private static final String IJM_PATH = "output/IJM/";
    private static final String MTD_PATH = "output/MTD/";
    private static final String casesPath = "cases.json";
    private final String REPOS = "/Users/pourya/IdeaProjects/RM-ASTDiff/tmp1"; //TODO: Modify this based on your local path
    private static Map<String, String> toolPathMap = new LinkedHashMap<>();
    private static List<CaseInfo> infos;
    private static Map<CaseInfo, Set<ASTDiff>> diffs = new LinkedHashMap<>();

    public BenchmarkMetrics() throws Exception {
        populateTools();
        fetchCases();
        runRMLocally();
    }

    private static void populateTools() {
//        toolPathMap.put("GOD", GOD_PATH);
        toolPathMap.put("RMD", RMD_PATH);
        toolPathMap.put("GTG", GTG_PATH);
        toolPathMap.put("GTS", GTS_PATH);
//        toolPathMap.put("IJM", IJM_PATH);
//        toolPathMap.put("MTD", MTD_PATH);
        System.out.println("Finished populating tools...");
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


    private void fetchCases() throws IOException {
        infos = mapper.readValue(new File(casesPath), new TypeReference<List<CaseInfo>>(){});
//        infos = new ArrayList<>();
//        infos.add(new CaseInfo("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825"));
        System.out.println("Finished fetching cases...");
    }

    private static final ObjectMapper mapper = new ObjectMapper();

    public List<DiffFileStats> generateBenchmarkStats() throws IOException {

        List<DiffFileStats> benchmarkStats = new ArrayList<>();
        for (CaseInfo info : infos) {
            String folderPath = exportedFolderPathByCaseInfo(info);
            String godFullFolderPath = GOD_PATH + folderPath;
            Path dir = Paths.get(godFullFolderPath);
            Files.walk(dir).filter(path -> path.toFile().isFile()).forEach(path ->
                    {
                        DiffFileStats diffFileStats = new DiffFileStats(info, path.getFileName().toString());
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
                            diffFileStats.putStats(entry.getKey(), diffStats);
                        }
                        DiffIgnore diffIgnore = new DiffIgnore(astDiff.src.getRoot(), astDiff.dst.getRoot(), astDiff.getMultiMappings());
                        diffIgnore.run();
                        diffFileStats.setIgnore(diffIgnore);
                        benchmarkStats.add(diffFileStats);
                    });
        }
        return benchmarkStats;
    }

    private static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff) {
        MetricComputer metricComputer = new MetricComputer(godDiff, toolDiff);
        return new DiffStats(metricComputer.programElementStats(), metricComputer.mappingStats());
    }
//    private static DiffStats compareHumanReadableDiffs(HumanReadableDiff godDiff, HumanReadableDiff toolDiff, Tree src, Tree dst, ExtendedMultiMappingStore extendedMultiMappingStore) {
//        MetricComputer metricComputer = new MetricComputer(godDiff, toolDiff);
//        metricComputer.advance(src,dst,extendedMultiMappingStore);
//        return new DiffStats(metricComputer.programElementStats(), metricComputer.mappingStats(),metricComputer.getNumberOfIgnoredElements(),metricComputer.getNumberOfIgnoredMappings());
//    }

    private static String exportedFolderPathByCaseInfo(CaseInfo info) {
        return repoFolder(info.getRepo()) +  "/" + info.commit;
    }
}
