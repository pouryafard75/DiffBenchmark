package benchmark.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.jgit.util.IO;

import java.io.File;
import java.io.IOException;
import java.util.*;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class Configuration {
    public final String perfectDiffDir;
    public final Set<CaseInfo> allCases;
    public final String output_folder = "output/";
    public transient final String PERFECT_DIFF_DIR = "perfect-diff/";

    public static final String REPOS = "/Users/pourya/IdeaProjects/RM-ASTDiff/tmp1/"; //TODO: Modify this based on your local path
    public static final String STATS_CSV = "stats.csv";
    public static Map<String, String> toolPathMap = new LinkedHashMap<>();

    public static final String GOD = "GOD";
    public static final String RMD = "RMD";
    public static final String GTG = "GTG";
    public static final String GTS = "GTS";
    public static final String IJM = "IJM";
    public static final String MTD = "MTD";
    private Configuration(String perfectDiffDir, String[] casesPath) throws IOException {
        this.perfectDiffDir = perfectDiffDir;
        allCases = new LinkedHashSet<>();
        ObjectMapper mapper = new ObjectMapper();
        for (String path : casesPath) {
            Set<CaseInfo> loaded = mapper.readValue(new File(path), new TypeReference<Set<CaseInfo>>(){});
            allCases.addAll(loaded);
        }
        System.out.println();
    }
    private Configuration(String perfectDiffDir, Set<CaseInfo> allCases) throws IOException {
        this.perfectDiffDir = perfectDiffDir;
        this.allCases = allCases;
    }
    static{
        toolPathMap.put("RMD", RMD);
        populateTools();
    }
    private static void populateTools() {
//        toolPathMap.put("GOD", GOD_PATH);
        toolPathMap.put("GTG", GTG);
        toolPathMap.put("GTS", GTS);
//        toolPathMap.put("IJM", IJM_PATH);
//        toolPathMap.put("MTD", MTD_PATH);
//        System.out.println("Finished populating tools...");
    }

    public static class ConfigurationFactory {
        private static final String REF_ORACLE_DIR = "/Users/pourya/IdeaProjects/RM-ASTDiff/src-test/data/astDiff/commits/";
        private static final String DEFECTS4J_ORACLE_DIR = "/Users/pourya/IdeaProjects/RM-ASTDiff/src-test/data/astDiff/defects4j/";
        private static final String perfectInfoName = "cases.json";
        private static final String problematicInfoName = "cases-problematic.json";
        private static final String TEST_URL = "https://github.com/phishman3579/java-algorithms-implementation/commit/f2385a56e6aa040ea4ff18a23ce5b63a4eeacf29";
        public static Configuration current() throws IOException {
            return dummy();
        }
        private static Configuration refOracle() throws IOException {
            return getConfiguration(REF_ORACLE_DIR);
        }
        private static Configuration defects4j() throws IOException {
            return getConfiguration(DEFECTS4J_ORACLE_DIR);
        }
        private static Configuration dummy() throws IOException{
            return new Configuration(REF_ORACLE_DIR,
                    Set.of(new CaseInfo(TEST_URL))
            );
        }
        private static Configuration getConfiguration(String perfectDiffDir) throws IOException {
            return new Configuration(perfectDiffDir, new String[]{
                    perfectDiffDir + perfectInfoName,
                    perfectDiffDir + problematicInfoName
            });
        }
    }
}
