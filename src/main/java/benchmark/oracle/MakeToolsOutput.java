package benchmark.oracle;

import benchmark.oracle.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.utils.Configuration.ConfigurationFactory;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {
    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.refOracle()).generate();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.refOracleTwoPointOne()).generate();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.defects4j()).generate();
        new BenchmarkHumanReadableDiffGenerator(ConfigurationFactory.defects4jTwoPointOne()).generate();
    }
}
