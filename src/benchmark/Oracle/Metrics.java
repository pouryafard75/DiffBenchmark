package benchmark.Oracle;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static benchmark.Oracle.OracleGenerator.replaceFileName;
import static benchmark.Oracle.OracleGenerator.repoFolder;

/* Created by pourya on 2023-04-03 1:51 a.m. */
public class Metrics {
    static int breakOn = 500;
    public static void main(String[] args) throws IOException {
        diffStatsAdvanced();
        System.out.println("******");
        diffStats();
    }

    private static void diffStats() throws IOException {
        DiffStats rm_diffStats = diffStats("cases.json", "output/God/", "output/RM/");
        DiffStats gtg_diffStats = diffStats("cases.json", "output/God/", "output/GTG/");
        DiffStats gts_diffStats = diffStats("cases.json", "output/God/", "output/GTS/");
        System.out.println("ProgramElementStats");
        System.out.println(AccPreRec(rm_diffStats.getProgramElement_stats()));
        System.out.println(AccPreRec(gtg_diffStats.getProgramElement_stats()));
        System.out.println(AccPreRec(gts_diffStats.getProgramElement_stats()));
        //
        System.out.println("---");
        //
        System.out.println("MappingStats");
        System.out.println(AccPreRec(rm_diffStats.getAbstractMapping_stats()));
        System.out.println(AccPreRec(gtg_diffStats.getAbstractMapping_stats()));
        System.out.println(AccPreRec(gts_diffStats.getAbstractMapping_stats()));
    }

    private static void diffStatsAdvanced() throws IOException {
        DiffStats rm_diffStats = diffStatsAdvanced("cases.json", "output/God/", "output/RM/");
        DiffStats gtg_diffStats = diffStatsAdvanced("cases.json", "output/God/", "output/GTG/");
        DiffStats gts_diffStats = diffStatsAdvanced("cases.json", "output/God/", "output/GTS/");
        System.out.println("ProgramElementStats");
        System.out.println(AccPreRec(rm_diffStats.getProgramElement_stats()));
        System.out.println(AccPreRec(gtg_diffStats.getProgramElement_stats()));
        System.out.println(AccPreRec(gts_diffStats.getProgramElement_stats()));
        //
        System.out.println("---");
        //
        System.out.println("MappingStats");
        System.out.println(AccPreRec(rm_diffStats.getAbstractMapping_stats()));
        System.out.println(AccPreRec(gtg_diffStats.getAbstractMapping_stats()));
        System.out.println(AccPreRec(gts_diffStats.getAbstractMapping_stats()));
    }

    private static List<Float> AccPreRec(List<Stats> stats) {
        float accSum, preSum, recSum;
        List<Float> AccPreRec = new ArrayList<>();
        int count;

        accSum = 0;
        preSum = 0;
        recSum = 0;
        count = 0;
        for (Stats stat : stats) {
            if (Float.isNaN(stat.calcAccuracy()) || Float.isNaN(stat.calcPrecision()) || Float.isNaN(stat.calcRecall()))
                continue;
            accSum += stat.calcAccuracy();
            preSum += stat.calcPrecision();
            recSum += stat.calcRecall();

            count += 1;
        }

        AccPreRec.add((float) ((float)accSum / (float)count));
        AccPreRec.add((float) ((float)preSum / (float)count));
        AccPreRec.add((float) ((float)recSum / (float)count));
        return AccPreRec;
    }

    private static DiffStats diffStats(String jsonFile, String godPath, String toolPath) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<CaseInfo> infos = mapper.readValue(new File(jsonFile), new TypeReference<List<CaseInfo>>(){});
        List<Stats> programElement_stats = new ArrayList<>();
        List<Stats> abstractMapping_stats = new ArrayList<>();
        int i = 0;
        for (CaseInfo info : infos) {
            if (i == breakOn) break;
            i += 1;
            String pathString = godPath + repoFolder(info.getRepo()) +  "/" + info.commit;
            Path dir = Paths.get(pathString);
            Files.walk(dir).filter(path -> path.toFile().isFile()).forEach(path ->
                    {
                        String godFullPath = path.toFile().toString();
                        String toolFullPath = godFullPath.replace(godPath, toolPath);
                        try {
                            HumanReadableDiff godDiff = mapper.readValue(new File(godFullPath), HumanReadableDiff.class);
                            HumanReadableDiff toolDiff = mapper.readValue(new File(toolFullPath), HumanReadableDiff.class);
                            MetricComputer metricComputer = new MetricComputer(godDiff, toolDiff);
                            programElement_stats.add(metricComputer.programElementStats());
                            abstractMapping_stats.add(metricComputer.mappingStats());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
        }
        return new DiffStats(abstractMapping_stats,programElement_stats);
    }

    private static DiffStats diffStatsAdvanced(String jsonFile, String godPath, String toolPath) throws IOException {

        ObjectMapper mapper = new ObjectMapper();
        List<CaseInfo> infos = mapper.readValue(new File(jsonFile), new TypeReference<List<CaseInfo>>(){});
        List<Stats> programElement_stats = new ArrayList<>();
        List<Stats> abstractMapping_stats = new ArrayList<>();
        int i = 0;
        for (CaseInfo info : infos) {
            if (i == breakOn) break;
            i += 1;
            Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(info.getRepo(), info.getCommit(), 1000);
            String pathString = godPath + repoFolder(info.getRepo()) +  "/" + info.commit;
            Path dir = Paths.get(pathString);
            Files.walk(dir).filter(path -> path.toFile().isFile()).forEach(path ->
            {
                String godFullPath = path.toFile().toString();
                String toolFullPath = godFullPath.replace(godPath, toolPath);
                try {
                    HumanReadableDiff godDiff = mapper.readValue(new File(godFullPath), HumanReadableDiff.class);
                    HumanReadableDiff toolDiff = mapper.readValue(new File(toolFullPath), HumanReadableDiff.class);
                    MetricComputer metricComputer = new MetricComputer(godDiff, toolDiff);

                    Tree srcTree = null;
                    Tree dstTree = null;
                    ExtendedMultiMappingStore mappings = null;
                    for (ASTDiff astDiff : astDiffs) {
                        String srcPath = astDiff.getSrcPath();
                        String replacedFileName = replaceFileName(srcPath);
                        if (replacedFileName.equals(path.toFile().getName()))
                        {
                            srcTree = astDiff.src.getRoot();
                            dstTree = astDiff.dst.getRoot();
                            mappings = astDiff.getMultiMappings();
                            break;
                        }
                    }
                    if (srcTree != null && dstTree != null)
                    metricComputer.advance(srcTree,dstTree,mappings);
                    programElement_stats.add(metricComputer.programElementStats());
                    abstractMapping_stats.add(metricComputer.mappingStats());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
        return new DiffStats(abstractMapping_stats,programElement_stats);
    }
}
