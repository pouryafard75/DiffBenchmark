package rm;


import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import rm.utils.URLHelper;
import rm.webdiff.WebDiff;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class Run {
    public static void main(String[] args) throws RefactoringMinerTimedOutException, IOException {
        String url;
        String folder1,folder2;
        Set<ASTDiff> astDiffs;

        url =  "https://github.com/pouryafard75/TestCases/commit/0ae8f723a59722694e394300656128f9136ef466";
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);

        astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);


        folder1 = "/Users/pourya/IdeaProjects/TestCases/case/v1/";
        folder2 = "/Users/pourya/IdeaProjects/TestCases/case/v2/";
//        astDiffs = new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(folder1),Path.of(folder2));
//
        new WebDiff(astDiffs).run();
//

    }
}
