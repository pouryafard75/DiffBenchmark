package benchmark.utils;

import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;

import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;
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
    public static ProjectASTDiff runWhatever(CaseInfo info) {
        return runWhatever(info.repo, info.commit);
    }

    public static rm2.refactoringminer.astDiff.actions.ProjectASTDiff runWhateverForRM2(CaseInfo caseInfo) {
        String repo = caseInfo.repo;
        String commit = caseInfo.commit;
        if (repo == null){
            if (caseInfo.srcPath != null && caseInfo.dstPath != null)
                return new rm2.refactoringminer.rm1.GitHistoryRefactoringMinerImpl().diffAtDirectories(new File(caseInfo.srcPath), new File(caseInfo.dstPath));
        }
        rm2.refactoringminer.astDiff.actions.ProjectASTDiff projectASTDiff;
        if (repo.contains("github")) {
            projectASTDiff = new rm2.refactoringminer.rm1.GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(ORACLE_DIR));
        }
        else{
            Path beforePath = Path.of(getBeforeDir(repo, commit));
            Path afterPath = Path.of(getAfterDir(repo, commit));
            projectASTDiff = new rm2.refactoringminer.rm1.GitHistoryRefactoringMinerImpl().diffAtDirectories(
                    beforePath, afterPath);
        }
        return projectASTDiff;
    }
}
///Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/astDiff/defects4j/Chart/1