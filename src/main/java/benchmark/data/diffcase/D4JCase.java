package benchmark.data.diffcase;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.nio.file.Path;

import static benchmark.data.dataset.EBenchmarkDataset.Defects4J;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2024-09-28*/
public class D4JCase extends LocalCase {
    final String project;
    final String bugID;

    @JsonCreator
    public D4JCase(@JsonProperty("repo") String project, @JsonProperty("commit") String bugID) {
        super(Path.of(getBeforeDir(project, bugID)), Path.of(getAfterDir(project, bugID)));
        this.project = project;
        this.bugID = bugID;
        this.dataset = Defects4J;
    }

    @Override
    public String getRepo() {
        return project;
    }

    @Override
    public String getCommit() {
        return bugID;
    }

    @Override
    public String getID() {
        return project + "/" + bugID;
    }
}
