package benchmark;

import com.github.gumtreediff.matchers.*;
import gui.utils.DiffUtils;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ProjectASTDiffer;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import shaded.com.github.gumtreediff.matchers.CompositeMatcher;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class Benchmark {
    public static void main(String[] args) throws IOException {
        String url = "https://github.com/pouryafard75/TestCases/commit/0ae8f723a59722694e394300656128f9136ef466";
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);
        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        for (ASTDiff astDiff : astDiffs) {
            OracleMaker rm = new OracleMaker(astDiff.src.getRoot(), astDiff.dst.getRoot(), MappingExportModel.exportModelList(astDiff.getMultiMappings()), astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.getSrcContents(), astDiff.getDstContents());
            rm.make();
            rm.write("out/RM/");

            MappingStore match = new CompositeMatchers.ClassicGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot());
            OracleMaker gt = new OracleMaker(astDiff.src.getRoot(), astDiff.dst.getRoot(), MappingExportModel.exportModelList(match), astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.getSrcContents(), astDiff.getDstContents());
            gt.make();
            gt.write("out/GT/");

            Set<Mapping> ijmDiff = DiffUtils.IJMDiff(astDiff.getSrcContents(), astDiff.getDstContents());
            OracleMaker ijm = new OracleMaker(astDiff.src.getRoot(), astDiff.dst.getRoot(), MappingExportModel.exportModelList(ijmDiff), astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.getSrcContents(), astDiff.getDstContents());
            ijm.make();
            ijm.write("out/IJM/");

            Set<Mapping> mtdiff = DiffUtils.MTDiff(astDiff.getSrcContents(), astDiff.getDstContents());
            OracleMaker mtd = new OracleMaker(astDiff.src.getRoot(), astDiff.dst.getRoot(), MappingExportModel.exportModelList(mtdiff), astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.getSrcContents(), astDiff.getDstContents());
            ijm.make();
            ijm.write("out/MTD/");

        }
    }

}
