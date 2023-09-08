package benchmark.others;

import benchmark.oracle.generators.PerfectDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.Set;

import static benchmark.utils.Configuration.REPOS;

/* Created by pourya on 2023-08-29 9:11 p.m. */
public class checkNPE {
    public static void main(String[] args) throws Exception {
        Configuration configuration = Configuration.ConfigurationFactory.current();
        Set<CaseInfo> infos = configuration.allCases;
        for (CaseInfo info : infos) {
            String repo = info.getRepo();
            String commit = info.getCommit();
            ProjectASTDiff projectASTDiff;
            Set<ASTDiff> astDiffs;
            if (repo.contains("github")) {
                GitService gitService = new GitServiceImpl();
                String repoFolder = repo.substring(repo.lastIndexOf("/"), repo.indexOf(".git"));
                Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, repo);
                try {
                    projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 100);
                    for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
                        PerfectDiff perfectDiff = new PerfectDiff(astDiff.getSrcPath(), projectASTDiff, repo, commit, configuration);
                        try {
                            perfectDiff.makeASTDiff();
                        }
                        catch (Exception e){
                            System.out.println("BUG BUG BUG " + repo + " " + commit);
                        }
                    }
                }
                catch (Exception e){
                    System.out.println("Exception in " + repo + " " + commit);
                }

            }
        }
    }
}
