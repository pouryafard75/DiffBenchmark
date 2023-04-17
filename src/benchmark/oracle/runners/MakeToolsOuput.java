package benchmark.oracle.runners;

import benchmark.oracle.utils.CaseInfo;
import benchmark.oracle.OracleGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.matchers.*;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class MakeToolsOuput {
    public static void main(String[] args) throws IOException {
        String jsonFile = "cases.json";
        ObjectMapper mapper = new ObjectMapper();
        List<CaseInfo> infos = mapper.readValue(new File(jsonFile), new TypeReference<List<CaseInfo>>(){});
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/real-logic/Aeron/commit/35893c115ba23bd62a7036a33390420f074ce660";
//        String repo = URLHelper.getRepo(url);
//        String commit = URLHelper.getCommit(url);
//        makeToolOutputs(repo,commit);

        for (CaseInfo info : infos) {
            String repo = info.getRepo();
            String commit = info.getCommit();
            makeToolOutputs(repo, commit);
        }
    }

    private static void makeToolOutputs(String repo, String commit) {
        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        for (ASTDiff astDiff : astDiffs) {
            OracleGenerator rmHD = new OracleGenerator(repo, commit, astDiff);
            rmHD.make();
            rmHD.write("output/RM/");
            OracleGenerator gtgHD = oracleGenFromMatch(new CompositeMatchers.ClassicGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot()), repo, commit, astDiff);
            gtgHD.write("output/GTG/");
            OracleGenerator gtsHD = oracleGenFromMatch(new CompositeMatchers.SimpleGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot()), repo, commit, astDiff);
            gtsHD.write("output/GTS/");
        }
    }

    private static OracleGenerator oracleGenFromMatch(MappingStore match, String repo, String commit, ASTDiff astDiff) {
        ExtendedMultiMappingStore GTG_mappingStore = new ExtendedMultiMappingStore(astDiff.src.getRoot(), astDiff.dst.getRoot());
        GTG_mappingStore.add(match);
        ASTDiff diff = new ASTDiff(astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.src, astDiff.dst, GTG_mappingStore);
        diff.setSrcContents(astDiff.getSrcContents());
        diff.setDstContents(astDiff.getDstContents());
        OracleGenerator toolHD = new OracleGenerator(repo, commit, diff);
        toolHD.make();
        return toolHD;
    }

}
