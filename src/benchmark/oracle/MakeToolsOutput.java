package benchmark.oracle;

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
        ObjectMapper mapper = new ObjectMapper();
        List<CaseInfo> infos = mapper.readValue(new File(Configuration.casesPath), new TypeReference<List<CaseInfo>>(){});
        infos = new ArrayList<>();
        infos.add(new CaseInfo("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825"));
        for (CaseInfo info : infos) {
            benchmarkHumanReadableDiffGenerator.writeToFiles(info);
        }
    }

}
