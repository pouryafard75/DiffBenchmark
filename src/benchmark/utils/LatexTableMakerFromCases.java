package benchmark.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static benchmark.utils.Configuration.casesPath;

/* Created by pourya on 2023-04-23 8:06 p.m. */
public class LatexTableMakerFromCases {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        List<CaseInfo> infos = mapper.readValue(new File(casesPath), new TypeReference<List<CaseInfo>>(){});
        StringBuilder sb = new StringBuilder();
        for (CaseInfo info : infos) {
            String repo = info.getRepo();
            repo = repo.replace("https://github.com/","").replace(".git","").replace("_","\\textunderscore ");
            sb.append(repo);
            sb.append(" & ");
            sb.append("\\href{" + info.makeURL() + "}{" + info.getCommit() + "}");
            sb.append(" \\\\");
            sb.append(" \\hline");
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
