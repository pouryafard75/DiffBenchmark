package benchmark.data.diffcase;

import benchmark.data.dataset.EBenchmarkDataset;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.nio.file.Path;

import static org.refactoringminer.astDiff.utils.URLHelper.nthIndexOf;

public class QCase extends AbstractIBenchmarkCase {

    private ProjectASTDiff projectASTDiff;
    String repo;
    String commit;
    String url;

    public QCase(String url) {
        if (url.contains(";"))
        {
            //the part before url is the repo, and the part after is the commit
            String[] parts = url.split(";");
            this.repo = parts[0];
            this.commit = parts[1];
            dataset = EBenchmarkDataset.Defects4J;
        }
        else {
            this.url = url;
            this.repo = URLHelper.getRepo(url);
            this.commit = URLHelper.getCommit(url);
            dataset = EBenchmarkDataset.RefOracle;
        }
    }

    @Override
    public String getRepo() {
        return this.repo;
    }

    @Override
    public String getCommit() {
        return this.commit;
    }

    @Override
    public String getID() {
        return this.url != null ? this.url : this.repo + ":" + this.commit;
    }

    @Override
    public ProjectASTDiff getProjectASTDiff() {
        //tmp, we migrate to diff driver ASAP
        if (projectASTDiff != null) {
            return projectASTDiff;
        }
        if (url == null)
        {
            return new D4JCase(repo, commit).getProjectASTDiff();
        }
        String repo = URLHelper.getRepo(url);
        String PR = getPRID(url);
        try {
            if (URLHelper.isPR(this.url))
                projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtPullRequest(repo, Integer.parseInt(PR), 10000000);
            else
                projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, URLHelper.getCommit(url), 10000000);
            return projectASTDiff;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    static String getPRID(String url) {
        int start = nthIndexOf(url,'/',6);
        int end = nthIndexOf(url,'/',7);
        if (end == -1) end = url.length();
        return url.substring(start+1, end);
    }

    @Override
    public Path getRelativePathFromDatasetDir() {
        throw new RuntimeException("Q case does not have a path");
    }

    @Override
    public String toString() {
        return this.url;
    }
}
