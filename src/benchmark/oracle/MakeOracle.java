package benchmark.oracle;

import benchmark.utils.CaseInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static benchmark.oracle.utils.generators.BenchmarkOracleGenerator.makeOutputs;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeOracle {
    public static void main(String[] args) throws IOException {
        String jsonFile = "cases.json";
//        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/real-logic/Aeron/commit/35893c115ba23bd62a7036a33390420f074ce660";
//        String repo = URLHelper.getRepo(url);
//        String commit = URLHelper.getCommit(url);
//        makeToolOutputs(repo,commit);
        ObjectMapper mapper = new ObjectMapper();
        List<CaseInfo> infos = mapper.readValue(new File(jsonFile), new TypeReference<List<CaseInfo>>(){});
        for (CaseInfo info : infos) {
            String repo = info.getRepo();
            String commit = info.getCommit();
            makeOutputs(repo, commit);
        }
    }

}
