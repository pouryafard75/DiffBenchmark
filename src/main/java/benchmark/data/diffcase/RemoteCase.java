package benchmark.data.diffcase;

import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.util.Objects;

import static benchmark.conf.Paths.ORACLE_DIR;

/* Created by pourya on 2024-09-28*/
public abstract class RemoteCase extends AbstractIBenchmarkCase {
    String repo;
    String commit;
    String url;

    public String getUrl() {
        return url;
    }

    public RemoteCase(String repo, String commit) {
        this.repo = repo;
        this.commit = commit;
    }
    public RemoteCase(String url) {
        this.repo = URLHelper.getRepo(url);
        this.commit = URLHelper.getCommit(url);
    }

    public String getRepo() {
        return repo;
    }

    public String getCommit() {
        return commit;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public void setCommit(String commit) {
            this.commit = commit;
        }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RemoteCase that = (RemoteCase) o;
        return Objects.equals(repo, that.repo) && Objects.equals(commit, that.commit);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(repo);
        result = 31 * result + Objects.hashCode(commit);
        return result;
    }

    @Override
    public ProjectASTDiff getProjectASTDiff() {
        return new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(this.getRepo(), this.getCommit(), new File(ORACLE_DIR));
    }
}
