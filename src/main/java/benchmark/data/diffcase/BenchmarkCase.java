package benchmark.data.diffcase;

import benchmark.data.dataset.IBenchmarkDataset;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.nio.file.Path;

public interface BenchmarkCase {
    String getRepo();
    String getCommit();
    String getID();
    ProjectASTDiff getProjectASTDiff();

    IBenchmarkDataset getDataset();
    void setDataset(IBenchmarkDataset benchmarkDataset); //To do forward referencing

    default Path getRelativePathFromDatasetDir() {
        return Path.of(getRepo(), getCommit()); //repo + "/" (separator) + commit;
    }
}
