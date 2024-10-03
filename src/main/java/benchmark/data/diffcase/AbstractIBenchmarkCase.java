package benchmark.data.diffcase;

import benchmark.data.dataset.IBenchmarkDataset;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

/* Created by pourya on 2024-09-29*/
public abstract class AbstractIBenchmarkCase implements IBenchmarkCase
{
    @JsonIgnore
    ProjectASTDiff projectASTDiff;
    @JsonIgnore
    IBenchmarkDataset dataset;

    @Override
    public IBenchmarkDataset getDataset() {
        return dataset;
    }

    public void setDataset(IBenchmarkDataset dataset) {
        this.dataset = dataset;
    }
    @Override
    public ProjectASTDiff getProjectASTDiff() {
        return projectASTDiff;
    }
}
