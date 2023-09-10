package benchmark.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class Configuration {
    public static final String GOD = "GOD";
    public static final String RMD = "RMD";
    public static final String GTG = "GTG";
    public static final String GTS = "GTS";
    public static final String IJM = "IJM";
    public static final String MTD = "MTD";
    public static Set<String> activeTools = new LinkedHashSet<>();
    private final String perfectDiffDir;
    private final Set<CaseInfo> allCases;
    private String outputFolder = "output/";
    private String csvDestinationFile = "stats.csv";

    public static final String REPOS = "/Users/pourya/IdeaProjects/RM-ASTDiff/tmp1/"; //Note: Modify this based on your local path

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
    }

    public void setCsvDestinationFile(String csvDestinationFile) {
        this.csvDestinationFile = csvDestinationFile;
    }

    private Configuration(String perfectDiffDir, String[] casesPath) throws IOException {
        this(perfectDiffDir, new LinkedHashSet<>());
        ObjectMapper mapper = new ObjectMapper();
        for (String path : casesPath) {
            Set<CaseInfo> loaded = mapper.readValue(new File(path), new TypeReference<Set<CaseInfo>>(){});
            getAllCases().addAll(loaded);
        }
    }
    private Configuration(String perfectDiffDir, Set<CaseInfo> allCases) throws IOException {
        this.perfectDiffDir = perfectDiffDir;
        this.allCases = allCases;
    }
    static{
        activeTools.add(RMD);
        populateTools();
    }
    private static void populateTools() {
//      activeTools.add(GOD);
        activeTools.add(GTG);
        activeTools.add(GTS);
//        activeTools.add(IJM);
//        activeTools.add(MTD);
//        System.out.println("Finished populating tools...");
    }
    public static String[] getActiveTools(){
        return activeTools.toArray(new String[0]);
    }

    public String getPerfectDiffDir() {
        return perfectDiffDir;
    }

    public Set<CaseInfo> getAllCases() {
        return allCases;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public String getCsvDestinationFile() {
        return csvDestinationFile;
    }

    public static class ConfigurationFactory {
        private static final String REF_ORACLE_DIR = "/Users/pourya/IdeaProjects/RM-ASTDiff/src-test/data/astDiff/commits/";
        private static final String DEFECTS4J_ORACLE_DIR = "/Users/pourya/IdeaProjects/RM-ASTDiff/src-test/data/astDiff/defects4j/";
        private static final String perfectInfoName = "cases.json";
        private static final String problematicInfoName = "cases-problematic.json";
//        private static final String TEST_URL = "https://github.com/Netflix/eureka/commit/f6212a7e474f812f31ddbce6d4f7a7a0d498b751";
//        private static final String TEST_URL = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        private static final String TEST_URL = "https://github.com/phishman3579/java-algorithms-implementation/commit/f2385a56e6aa040ea4ff18a23ce5b63a4eeacf29";
        private static final String TEST_URL = "https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e";

        public static Configuration getDefault() throws IOException {
            return refOracle();
        }
        private static Configuration refOracle() throws IOException {
            Configuration configuration = getConfiguration(REF_ORACLE_DIR);
            configuration.setOutputFolder("output-refOracle/");
            configuration.setCsvDestinationFile("stats-refOracle.csv/");
            return configuration;
        }
        private static Configuration defects4j() throws IOException {
            Configuration configuration = getConfiguration(DEFECTS4J_ORACLE_DIR);
            configuration.setOutputFolder("output-defects4j/");
            configuration.setCsvDestinationFile("stats-defects4j.csv/");
            return configuration;

        }
        private static Configuration dummy() throws IOException{
            Configuration configuration = new Configuration(REF_ORACLE_DIR,
                    Set.of(new CaseInfo(TEST_URL))
            );
            configuration.setOutputFolder("output-dummy/");
            configuration.setCsvDestinationFile("stats-dummy.csv/");
            return configuration;
        }
        private static Configuration getConfiguration(String perfectDiffDir) throws IOException {
            return new Configuration(perfectDiffDir, new String[]{
                    perfectDiffDir + perfectInfoName,
                    perfectDiffDir + problematicInfoName
            });
        }
    }
}
