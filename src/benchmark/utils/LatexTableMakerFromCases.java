package benchmark.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-04-23 8:06 p.m. */
public class LatexTableMakerFromCases {
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        Set<CaseInfo> infos = Configuration.ConfigurationFactory.getDefault().getAllCases();
        StringBuilder sb = new StringBuilder();
        for (CaseInfo info : infos) {
            String repo = info.getRepo();
            repo = repo.replace("https://github.com/","").replace(".git","").replace("_","\\textunderscore ");
            sb.append(repo);
            sb.append(" & ");
            sb.append("\\href{").append(info.makeURL()).append("}{").append(info.getCommit()).append("}");
            sb.append(" \\\\");
            sb.append(" \\hline");
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
