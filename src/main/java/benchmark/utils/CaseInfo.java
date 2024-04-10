package benchmark.utils;

import org.refactoringminer.astDiff.utils.URLHelper;

import java.io.Serializable;
import java.util.Set;

public class CaseInfo implements Serializable {
    String repo;
    String commit;
    String srcPath;
    String dstPath;
    Set<String> src_files;

    public void setSrc_files(Set<String> src_files) {
        this.src_files = src_files;
    }

    public Set<String> getSrc_files() {
        return src_files;
    }

    public static CaseInfo fromDirs(String srcPath, String dstPath){
        CaseInfo info = new CaseInfo();
        info.srcPath = srcPath;
        info.dstPath = dstPath;
        return info;
    }

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