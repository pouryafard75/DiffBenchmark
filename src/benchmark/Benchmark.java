package benchmark;

import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ProjectASTDiffer;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.util.Set;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class Benchmark {
    public static void main(String[] args) {
        String url = "https://github.com/pouryafard75/TestCases/commit/0ae8f723a59722694e394300656128f9136ef466";
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);
        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        for (ASTDiff astDiff : astDiffs) {
            OracleMaker oracleMaker = new OracleMaker(astDiff.src.getRoot(), astDiff.dst.getRoot(), MappingExportModel.exportModelList(astDiff.getMultiMappings()), astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.getSrcContents(), astDiff.getDstContents());
            oracleMaker.make();
            oracleMaker.write();
        }
    }

}
