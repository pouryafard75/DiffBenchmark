package benchmark.utils;

import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;

import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;


/* Created by pourya on 2023-04-17 8:53 p.m. */
public class Helpers {
    static String REPOS = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/oracle/commits";
    public static Diff diffByGumTree(ASTDiff astDiff, CompositeMatchers.CompositeMatcher matcher) {
        MappingStore match = matcher.match(astDiff.src.getRoot(), astDiff.dst.getRoot());
        EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
        return new Diff(astDiff.src, astDiff.dst, match, actions);
    }
    public static ProjectASTDiff runWhatever(String repo, String commit) {
        ProjectASTDiff projectASTDiff;
        if (repo.contains("github")) {
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(REPOS));
        }
        else{
            Path beforePath = Path.of(getBeforeDir(repo, commit));
            Path afterPath = Path.of(getAfterDir(repo, commit));
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtDirectories(
                    beforePath, afterPath);
        }
        return projectASTDiff;
    }
}
