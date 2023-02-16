package gui;

import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import gui.utils.URLHelper;
import gui.webdiff.WebDiff;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class RunWithGitHubAPI {
    public static void main(String[] args) throws RefactoringMinerTimedOutException, IOException {
        String url = "https://github.com/JetBrains/intellij-community/commit/7ed3f273ab0caf0337c22f0b721d51829bb0c877";
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);

        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        new WebDiff(astDiffs).run();
    }
}
