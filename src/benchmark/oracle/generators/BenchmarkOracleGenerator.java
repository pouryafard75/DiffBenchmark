package benchmark.oracle.generators;

import com.github.gumtreediff.matchers.*;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.util.Set;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkOracleGenerator {
    public static void writeToFiles(String repo, String commit) {
        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        for (ASTDiff astDiff : astDiffs) {
            HumanReadableDiffGenerator rmHD = new HumanReadableDiffGenerator(repo, commit, astDiff);
            rmHD.make();
            rmHD.write("output/RM/");
            HumanReadableDiffGenerator gtgHD = oracleGeneratorFromMappingStore(new CompositeMatchers.ClassicGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot()), repo, commit, astDiff);
            gtgHD.write("output/GTG/");
            HumanReadableDiffGenerator gtsHD = oracleGeneratorFromMappingStore(new CompositeMatchers.SimpleGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot()), repo, commit, astDiff);
            gtsHD.write("output/GTS/");
        }
    }

    private static HumanReadableDiffGenerator oracleGeneratorFromMappingStore(MappingStore match, String repo, String commit, ASTDiff astDiff) {
        ExtendedMultiMappingStore GTG_mappingStore = new ExtendedMultiMappingStore(astDiff.src.getRoot(), astDiff.dst.getRoot());
        GTG_mappingStore.add(match);
        ASTDiff diff = new ASTDiff(astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.src, astDiff.dst, GTG_mappingStore);
        diff.setSrcContents(astDiff.getSrcContents());
        diff.setDstContents(astDiff.getDstContents());
        HumanReadableDiffGenerator toolHD = new HumanReadableDiffGenerator(repo, commit, diff);
        toolHD.make();
        return toolHD;
    }
}
