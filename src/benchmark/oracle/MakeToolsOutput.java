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
//        infos = new ArrayList<>();
//        infos.add(new CaseInfo("https://github.com/raphw/byte-buddy/commit/f1dfb66a368760e77094ac1e3860b332cf0e4eb5"));
        for (CaseInfo info : infos) {
            benchmarkHumanReadableDiffGenerator.generate(info);
        }
    }

}
