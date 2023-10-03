package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.util.GitServiceImpl;

import java.io.IOException;

/* Created by pourya on 2023-05-02 5:15 p.m. */
public class CompareWithLocallyClonedRepository {
    public static void main(String[] args) throws IOException {

        String url = "https://github.com/JetBrains/intellij-community/commit/7ed3f273ab0caf0337c22f0b721d51829bb0c877";
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);
        BenchmarkWebDiff benchmarkWebDiff = null;
        GitService gitService = new GitServiceImpl();
        String projectName = repo.substring(repo.lastIndexOf("/") + 1, repo.length() - 4);
        String pathToClonedRepository = "tmp/" + projectName;

        try {
            Repository repository = gitService.cloneIfNotExists(pathToClonedRepository, repo);
            benchmarkWebDiff = BenchmarkWebDiffFactory.withLocallyClonedRepo(repository, commit);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        benchmarkWebDiff.run();
    }



}
