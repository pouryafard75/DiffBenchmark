package benchmark.oracle;

import benchmark.defects4j.Defect4jCaseInfo;
import benchmark.oracle.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {
    public static void main(String[] args) throws Exception {
        BenchmarkHumanReadableDiffGenerator benchmarkHumanReadableDiffGenerator = new BenchmarkHumanReadableDiffGenerator
                (Configuration.ConfigurationFactory.current());
        benchmarkHumanReadableDiffGenerator.generate();
    }

}
