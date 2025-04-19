package benchmark.data.diffcase;

import benchmark.data.dataset.EBenchmarkDataset;
import benchmark.data.dataset.IBenchmarkDataset;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;

import static benchmark.conf.Paths.ORACLE_DIR;


/* Created by pourya on 2024-09-28*/
public class GithubCase extends RemoteCase {
    @JsonCreator
    public GithubCase(@JsonProperty("repo") String repo, @JsonProperty("commit") String commit) {
        super(repo, commit);
        validate();
    }

    public GithubCase(String url) {
        super(url);
        validate();
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
    public String makeURL() {
        if (isGitHub())
            return this.getRepo().replace(".git","") + "/commit/" + this.getCommit();
        else
            return this.getRepo() + "/" + this.getCommit();
    }
}
