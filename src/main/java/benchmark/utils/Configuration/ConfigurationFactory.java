package benchmark.utils.Configuration;

import benchmark.utils.CaseInfo;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-09-18 1:39 a.m. */
public class ConfigurationFactory {
    // Update value to the RefactoringMiner cloned repository path in your hard drive
    private static final String REFACTORING_MINER_PATH = "/Users/pourya/IdeaProjects/RM-ASTDiff/";

    public static final String FINALIZED_REFACTORING_MINER_PATH = System.getProperty("refactoringMinerPath", REFACTORING_MINER_PATH);

    public static final String ORACLE_DIR = FINALIZED_REFACTORING_MINER_PATH + "/src/test/resources/oracle/commits/";

    private static final String REFACTORING_MAPPINGS_DIR = FINALIZED_REFACTORING_MINER_PATH + "/src/test/resources/astDiff/commits/";

    private static final String DEFECTS4J_MAPPING_DIR = FINALIZED_REFACTORING_MINER_PATH + "/src/test/resources/astDiff/defects4j/";

    private static final String perfectInfoName = "cases.json";
    private static final String problematicInfoName = "cases-problematic.json";
    private static final String TEST_URL = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
    private static final Set<CaseInfo> dummySet = Set.of(
            new CaseInfo("https://github.com/Activiti/Activiti/commit/a70ca1d9ad2ea07b19c5e1f9540c809d7a12d3fb"),
            new CaseInfo("https://github.com/Activiti/Activiti/commit/ca7d0c3b33a0863bed04c77932b9ef6b1317f34"),
            new CaseInfo("https://github.com/Alluxio/alluxio/commit/0ba343846f21649e29ffc600f30a7f3e463fb24c"),
            new CaseInfo("https://github.com/Alluxio/alluxio/commit/5b184ac783784c1ca4baf1437888c79bd9460763"),
            new CaseInfo("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825"),
            new CaseInfo("https://github.com/Alluxio/alluxio/commit/b0938501f1014cf663e33b44ed5bb9b24d19a358")
    );
    //    private static final String TEST_URL = "https://github.com/phishman3579/java-algorithms-implementation/commit/ab98bcacf6e5bf1c3a06f6bcca68f178f880ffc9";
//    private static final String TEST_URL = "https://github.com/JetBrains/intellij-community/commit/a9379ee529ed87e28c0736c3c6657dcd6a0680e4";
//    private static final String TEST_URL = "https://github.com/crate/crate/commit/72b5348";
//    private static final String TEST_URL = "https://github.com/Activiti/Activiti/commit/a70ca1d9ad2ea07b19c5e1f9540c809d7a12d3fb";
//    private static final String TEST_URL = "https://github.com/AsyncHttpClient/async-http-client/commit/f01d8610b9ceebc1de59d42f569b8af3efbe0a0f";


    public static Configuration getDefault(){
            return refOracleTwoPointOne();
    }
    public static Configuration experiment() {
        String confName = "experiment-0.0";
        return getConfTemplate(confName,
                REFACTORING_MAPPINGS_DIR,
                Compatibility.Experiment,
                GenerationStrategy.NonFiltered);

    }
    public static Configuration refOracle() {
        String confName = "refOracle-3.0";
        return getConfTemplate(confName,
                REFACTORING_MAPPINGS_DIR,
                Compatibility.ThreePointZero,
                GenerationStrategy.NonFiltered);
    }
    public static Configuration refOracleTwoPointOne() {
        String confName = "refOracle-2.1";
        return getConfTemplate(confName,
                REFACTORING_MAPPINGS_DIR,
                Compatibility.TwoPointOne,
                GenerationStrategy.Filtered);
    }
    public static Configuration defects4j() {
        String confName = "defects4j-3.0";
        return getConfTemplate(confName,
                DEFECTS4J_MAPPING_DIR,
                Compatibility.ThreePointZero,
                GenerationStrategy.NonFiltered);
    }

    public static Configuration defects4jTwoPointOne() {
        String confName = "defects4j-2.1";
        return getConfTemplate(confName,
                DEFECTS4J_MAPPING_DIR,
                Compatibility.TwoPointOne,
                GenerationStrategy.Filtered);
    }
    public static Configuration dummy() {
        String confName = "dummy3.0";
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setPerfectDiffDir(REFACTORING_MAPPINGS_DIR)
//                .setPerfectDiffDir(REF_ORACLE_DIR)
//                .setAllCases(Set.of(new CaseInfo(TEST_URL)))
                .setAllCases(dummySet)
//                .setAllCases(Set.of(new CaseInfo("Chart", "1"), new CaseInfo("Chart", "2")))
                .setCompatibility(Compatibility.ThreePointZero)
//                .setTools(Set.of(ASTDiffTool.GOD, ASTDiffTool.TRV, ASTDiffTool.RMD, ASTDiffTool.DAT))
                .setGenerationStrategy(GenerationStrategy.NonFiltered);
        return setOutputPaths(confName, configurationBuilder);
    }

    public static Configuration dummyTwoPointOne() {
        String confName = "dummy2.1";
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setPerfectDiffDir(REFACTORING_MAPPINGS_DIR)
//                .setAllCases(Set.of(new CaseInfo(TEST_URL)))
                .setAllCases(dummySet)
                .setCompatibility(Compatibility.TwoPointOne)
                .setGenerationStrategy(GenerationStrategy.Filtered);
        return setOutputPaths(confName, configurationBuilder);
    }

    private static Configuration getConfTemplate(String confName, String perfectDiffDir, Compatibility compatibility, GenerationStrategy generationStrategy) {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setPerfectDiffDir(perfectDiffDir)
                .setAllCases(new String[]{ perfectDiffDir + perfectInfoName, perfectDiffDir + problematicInfoName })
                .setCompatibility(compatibility)
                .setGenerationStrategy(generationStrategy);
        return setOutputPaths(confName, configurationBuilder);
    }

    private static Configuration setOutputPaths(String confName, ConfigurationBuilder configurationBuilder) {
        Configuration configuration = configurationBuilder.createConfiguration();
        configuration.setOutputFolder("oracle/output-" + confName);
        configuration.setCsvDestinationFile("stats.csv/");
        configuration.setName(confName);
        return configuration;
    }

    public static Configuration getConfByName(String arg) {
        switch (arg) {
            case "refOracle":
                return refOracle();
            case "refOracleTwoPointOne":
                return refOracleTwoPointOne();
            case "defects4j":
                return defects4j();
            case "defects4jTwoPointOne":
                return defects4jTwoPointOne();
            case "dummy":
                return dummy();
            case "dummyTwoPointOne":
                return dummyTwoPointOne();
        }
        return null;
    }
}