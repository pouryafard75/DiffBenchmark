package benchmark.utils.Configuration;

import benchmark.utils.CaseInfo;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-09-18 1:39 a.m. */
public class ConfigurationFactory {
    // Update value to the RefactoringMiner cloned repository path in your hard drive
    private static final String REFACTORING_MINER_PATH = "/Users/pourya/IdeaProjects/RM-ASTDiff/";

    public static final String ORACLE_DIR = REFACTORING_MINER_PATH + "/src/test/resources/oracle/commits/";

    private static final String REFACTORING_MAPPINGS_DIR = REFACTORING_MINER_PATH + "/src/test/resources/astDiff/commits/";

    private static final String DEFECTS4J_MAPPING_DIR = REFACTORING_MINER_PATH + "/src/test/resources/astDiff/defects4j/";

    private static final String perfectInfoName = "cases.json";
    private static final String problematicInfoName = "cases-problematic.json";
    private static final String TEST_URL = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//    private static final String TEST_URL = "https://github.com/Activiti/Activiti/commit/a70ca1d9ad2ea07b19c5e1f9540c809d7a12d3fb";

    public static Configuration getDefault() throws IOException {
            return refOracleTwoPointOne();
    }
    public static Configuration refOracle() throws IOException {
        String confName = "refOracle-3.0";
        return getConfTemplate(confName,
                REFACTORING_MAPPINGS_DIR,
                Compatibility.ThreePointZero,
                GenerationStrategy.NonFiltered);
    }
    public static Configuration refOracleTwoPointOne() throws IOException {
        String confName = "refOracle-2.1";
        return getConfTemplate(confName,
                REFACTORING_MAPPINGS_DIR,
                Compatibility.TwoPointOne,
                GenerationStrategy.Filtered);
    }
    public static Configuration defects4j() throws IOException {
        String confName = "defects4j-3.0";
        return getConfTemplate(confName,
                DEFECTS4J_MAPPING_DIR,
                Compatibility.ThreePointZero,
                GenerationStrategy.NonFiltered);
    }

    public static Configuration defects4jTwoPointOne() throws IOException {
        String confName = "defects4j-2.1";
        return getConfTemplate(confName,
                DEFECTS4J_MAPPING_DIR,
                Compatibility.TwoPointOne,
                GenerationStrategy.Filtered);
    }
    public static Configuration dummy() throws IOException{
        String confName = "dummy3.0";
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setPerfectDiffDir(REFACTORING_MAPPINGS_DIR)
//                .setPerfectDiffDir(REF_ORACLE_DIR)
                .setAllCases(Set.of(new CaseInfo(TEST_URL)))
//                .setAllCases(Set.of(new CaseInfo("Chart", "1"), new CaseInfo("Chart", "2")))
                .setCompatibility(Compatibility.ThreePointZero)
                .setGenerationStrategy(GenerationStrategy.NonFiltered);
        return setOutputPaths(confName, configurationBuilder);
    }

    public static Configuration dummyTwoPointOne() throws IOException{
        String confName = "dummy2.1";
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setPerfectDiffDir(REFACTORING_MAPPINGS_DIR)
                .setAllCases(Set.of(new CaseInfo(TEST_URL)))
                .setCompatibility(Compatibility.TwoPointOne)
                .setGenerationStrategy(GenerationStrategy.Filtered);
        return setOutputPaths(confName, configurationBuilder);
    }



    private static Configuration getConfTemplate(String confName, String perfectDiffDir, Compatibility compatibility, GenerationStrategy generationStrategy) throws IOException {
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setPerfectDiffDir(perfectDiffDir)
                .setAllCases(new String[]{ perfectDiffDir + perfectInfoName, perfectDiffDir + problematicInfoName })
                .setCompatibility(compatibility)
                .setGenerationStrategy(generationStrategy);
        return setOutputPaths(confName, configurationBuilder);
    }

    private static Configuration setOutputPaths(String confName, ConfigurationBuilder configurationBuilder) throws IOException {
        Configuration configuration = configurationBuilder.createConfiguration();
        configuration.setOutputFolder("output-" + confName);
        configuration.setCsvDestinationFile("stats.csv/");
        configuration.setName(confName);
        return configuration;
    }
}