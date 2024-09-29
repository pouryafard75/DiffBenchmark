package benchmark.data.diffcase;

import benchmark.data.dataset.IBenchmarkDataset;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

/* Created by pourya on 2024-09-28*/
public class RefCountCase implements BenchmarkCase {
    final int refCount;

    public RefCountCase(int refCount) {
        this.refCount = refCount;
    }

    @Override
    public String getRepo() {
        return "RefCount";
    }

    @Override
    public String getCommit() {
        return String.valueOf(refCount);
    }

    @Override
    public String getID() {
        return "RefCount/" + refCount;
    }

    @Override
    public ProjectASTDiff getProjectASTDiff() {
        throw new RuntimeException("This is an artificial case");
    }

    @Override
    public IBenchmarkDataset getDataset() {
        return null;
    }

    @Override
    public void setDataset(IBenchmarkDataset benchmarkDataset) {

    }
}
