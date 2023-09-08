package benchmark.utils;

import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.nio.file.Path;

import static benchmark.utils.Configuration.REPOS;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;


/* Created by pourya on 2023-04-17 8:53 p.m. */
public class Helpers {
    public static Diff diffByGumTree(ASTDiff astDiff, CompositeMatchers.CompositeMatcher matcher) {
        MappingStore match = matcher.match(astDiff.src.getRoot(), astDiff.dst.getRoot());
        EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
        return new Diff(astDiff.src, astDiff.dst, match, actions);
    }
    public static ProjectASTDiff make(String repo, String commit) throws Exception {
        ProjectASTDiff projectASTDiff;
        if (repo.contains("github")) {
            GitService gitService = new GitServiceImpl();
            String repoFolder = repo.substring(repo.lastIndexOf("/"), repo.indexOf(".git"));
            Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, repo);
//            astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 100);
        }
        else{
            String projectDir = repo;
            String bugID = commit;
            Path beforePath = Path.of(getBeforeDir(projectDir, bugID));
            Path afterPath = Path.of(getAfterDir(projectDir, bugID));
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtDirectories(
                    beforePath, afterPath);
        }
        return projectASTDiff;
    }
}
