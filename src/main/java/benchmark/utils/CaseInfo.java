package benchmark.utils;

import org.refactoringminer.astDiff.utils.URLHelper;

import java.io.Serializable;

public class CaseInfo implements Serializable {
    String repo;
    String commit;


    public CaseInfo(String repo, String commit) {
        this.repo = repo;
        this.commit = commit;
    }
    public CaseInfo(String url) {
        this.repo = URLHelper.getRepo(url);
        this.commit = URLHelper.getCommit(url);
    }

    public CaseInfo() {
    }

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public String getCommit() {
        return commit;
    }

    public void setCommit(String commit) {
        this.commit = commit;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof CaseInfo)) return false;
        CaseInfo second = (CaseInfo) obj;
        return second.getRepo().equals(this.getRepo()) &&
                second.getCommit().equals(this.getCommit());
    }

    public boolean isGitHub() {
        return this.repo.contains("github");
    }

    public String makeURL() {
        if (isGitHub())
            return this.repo.replace(".git","") + "/commit/" + this.commit;
        else
            return this.repo + "/" + this.commit;
    }

}