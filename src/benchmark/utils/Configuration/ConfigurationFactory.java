package benchmark.utils.Configuration;

import benchmark.utils.CaseInfo;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-09-18 1:39 a.m. */
public class ConfigurationFactory {
    private static final String REF_ORACLE_DIR = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/astDiff/commits/";
    private static final String DEFECTS4J_ORACLE_DIR = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/astDiff/defects4j/";
    private static final String perfectInfoName = "cases.json";
    private static final String problematicInfoName = "cases-problematic.json";
//    private static final String TEST_URL = "https://github.com/orientechnologies/orientdb/commit/1089957b645bde069d3864563bbf1f7c7da8045c";
//    private static final String TEST_URL = "https://github.com/Netflix/eureka/commit/f6212a7e474f812f31ddbce6d4f7a7a0d498b751";
    private static final String TEST_URL = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//    private static final String TEST_URL = "https://github.com/liferay/liferay-plugins/commit/7c7ecf4cffda166938efd0ae34830e2979c25c73";
//    private static final String TEST_URL = "https://github.com/phishman3579/java-algorithms-implementation/commit/f2385a56e6aa040ea4ff18a23ce5b63a4eeacf29";
//    private static final String TEST_URL = "https://github.com/apache/hive/commit/5f78f9ef1e6c798849d34cc66721e6c1d9709b6f";
//    private static final String TEST_URL = "https://github.com/eclipse/jetty.project/commit/837d1a74bb7d694220644a2539c4440ce55462cf";
//    private static final String TEST_URL = "https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e";
//    private static final String TEST_URL = "https://github.com/processing/processing/commit/f36b736cf1206dd1af794d6fb4cee967a3553b1f";
//    private static final String TEST_URL = "https://github.com/processing/processing/commit/acf67c8cb58d13827e14bbeeec11a66f9277015f";
//    private static final String TEST_URL = "https://github.com/deeplearning4j/deeplearning4j/commit/3d080545362794ac5ab63a6cf1bdfb523a0d92a5";
//    private static final String TEST_URL = "https://github.com/spring-projects/spring-data-mongodb/commit/3224fa8ce7e0079d6ad507e17534cdf01f758876";
//    private static final String TEST_URL = "https://github.com/pouryafard75/TestCases/commit/a0b10eee67fea5705947b89245eeedfc8b6a71ab";
//    private static final String TEST_URL = "https://github.com/real-logic/Aeron/commit/35893c115ba23bd62a7036a33390420f074ce660";

    public static Configuration getDefault() throws IOException {
            return dummy();
    }
    public static Configuration refOracle() throws IOException {
        String confName = "refOracle-3.0";
        return getConfTemplate(confName,
                REF_ORACLE_DIR,
                Compatibility.ThreePointZero,
                GenerationStrategy.NonFiltered);
    }
    public static Configuration refOracleTwoPointOne() throws IOException {
        String confName = "refOracle-2.1";
        return getConfTemplate(confName,
                REF_ORACLE_DIR,
                Compatibility.TwoPointOne,
                GenerationStrategy.Filtered);
    }
    public static Configuration defects4j() throws IOException {
        String confName = "defects4j-3.0";
        return getConfTemplate(confName,
                DEFECTS4J_ORACLE_DIR,
                Compatibility.ThreePointZero,
                GenerationStrategy.NonFiltered);
    }

    public static Configuration defects4jTwoPointOne() throws IOException {
        String confName = "defects4j-2.1";
        return getConfTemplate(confName,
                DEFECTS4J_ORACLE_DIR,
                Compatibility.TwoPointOne,
                GenerationStrategy.Filtered);
    }
    public static Configuration dummy() throws IOException{
        String confName = "dummy3.0";
        ConfigurationBuilder configurationBuilder = new ConfigurationBuilder()
                .setPerfectDiffDir(REF_ORACLE_DIR)
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
                .setPerfectDiffDir(REF_ORACLE_DIR)
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