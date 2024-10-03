package benchmark.data.dataset;

import benchmark.data.diffcase.IBenchmarkCase;

import java.nio.file.Path;
import java.util.Set;


/* Created by pourya on 2024-09-28*/
public enum EBenchmarkDataset implements IBenchmarkDataset{
    RefOracle(new RefactoringOracleBenchmarkDataset()),
    Defects4J(new Defects4JBenchmarkDataset()),
    Dummy(new DummyDataset());

    private final IBenchmarkDataset iBenchmarkDataset;

    EBenchmarkDataset(IBenchmarkDataset iBenchmarkDataset ) {
        this.iBenchmarkDataset = iBenchmarkDataset;
    }

    @Override
    public String getDatasetName() {
        return iBenchmarkDataset.getDatasetName();
    }

    @Override
    public Path getPerfectDirPath() {
        return iBenchmarkDataset.getPerfectDirPath();
    }

    @Override
    public Set<? extends IBenchmarkCase> getCases() {
        return iBenchmarkDataset.getCases();
    }
}
