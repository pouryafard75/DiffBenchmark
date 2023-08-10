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
        BenchmarkHumanReadableDiffGenerator benchmarkHumanReadableDiffGenerator = new BenchmarkHumanReadableDiffGenerator();
        //Check the config file before running this
        List<CaseInfo> infos = new ObjectMapper().readValue(new File(Configuration.casesPath), new TypeReference<List<CaseInfo>>(){});
        infos = new ArrayList<>();
//        infos.add(new CaseInfo("Lang", "58"));
//        infos.add(new CaseInfo("https://github.com/brettwooldridge/HikariCP/commit/cd8c4d578a609bdd6395d3a8c49bfd19ed700dea"));
        infos.add(new CaseInfo("https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e"));
        for (CaseInfo info : infos) {
            benchmarkHumanReadableDiffGenerator.generate(info);
        }
    }

}
