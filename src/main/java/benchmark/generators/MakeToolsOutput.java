package benchmark.generators;

import benchmark.utils.Configuration.ConfigurationFactory;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {

    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.hack_defects4j_3()).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.hack_refOracle_3()).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.refOracle()).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.refOracleTwoPointOne()).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.defects4j()).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.defects4jTwoPointOne()).generateMultiThreaded();
    }
}
