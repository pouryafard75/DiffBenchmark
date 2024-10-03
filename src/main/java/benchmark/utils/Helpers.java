package benchmark.utils;

import benchmark.data.diffcase.IBenchmarkCase;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;

import static benchmark.conf.Paths.ORACLE_DIR;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;


/* Created by pourya on 2023-04-17 8:53 p.m. */
public class Helpers {
    public static ProjectASTDiff runWhatever(String repo, String commit) {
        ProjectASTDiff projectASTDiff;
        if (repo.contains("github")) {
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(ORACLE_DIR));
        }
        else{
            Path beforePath = Path.of(getBeforeDir(repo, commit));
            Path afterPath = Path.of(getAfterDir(repo, commit));
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtDirectories(
                    beforePath, afterPath);
        }
        return projectASTDiff;
    }
    public static ProjectASTDiff runWhatever(IBenchmarkCase info) {
        return runWhatever(info.getRepo(), info.getCommit());
    }
}