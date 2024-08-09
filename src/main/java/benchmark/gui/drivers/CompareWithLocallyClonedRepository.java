package benchmark.gui.drivers;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.util.GitServiceImpl;

import java.io.IOException;

/* Created by pourya on 2023-05-02 5:15 p.m. */
public class CompareWithLocallyClonedRepository {
    public static void main(String[] args) throws Exception {
        String repo = "https://github.com/Alluxio/alluxio.git";
        String commit = "9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
        String pathToClonedRepository = "tmp/" + "Alluxio/Alluxio";
        Repository repository = new GitServiceImpl().cloneIfNotExists(pathToClonedRepository, repo);
        new BenchmarkWebDiffFactory().withLocallyClonedRepo(repository, commit).run();
    }
}
