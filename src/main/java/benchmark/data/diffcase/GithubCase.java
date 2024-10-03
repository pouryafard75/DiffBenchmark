package benchmark.data.diffcase;

import benchmark.data.dataset.EBenchmarkDataset;
import benchmark.data.dataset.IBenchmarkDataset;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

import static benchmark.conf.Paths.ORACLE_DIR;


/* Created by pourya on 2024-09-28*/
public class GithubCase extends RemoteCase {



    @JsonCreator
    public GithubCase(@JsonProperty("repo") String repo, @JsonProperty("commit") String commit) {
        super(repo, commit);
        validateAndComputeProjectASTDiff(repo, commit);
    }

    public GithubCase(String url) {
        super(url);
        validateAndComputeProjectASTDiff(repo, commit);
    }

    private void validateAndComputeProjectASTDiff(String repo, String commit) {
        validate();
        projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(ORACLE_DIR));
        System.out.println("Finished computing projectASTDiff for " + repo + " " + commit);
    }

    private void validate() {
        if (!isGitHub())
            throw new IllegalArgumentException("Not a GitHub URL");
    }


    @Override
    public String getID() {
        return makeURL();
    }

    @Override
    public IBenchmarkDataset getDataset() {
        return EBenchmarkDataset.RefOracle;
    }

    boolean isGitHub() {
        return this.getRepo().contains("github");
    }
    String makeURL() {
        if (isGitHub())
            return this.getRepo().replace(".git","") + "/commit/" + this.getCommit();
        else
            return this.getRepo() + "/" + this.getCommit();
    }

    public static void main(String[] args) {

    }
}
