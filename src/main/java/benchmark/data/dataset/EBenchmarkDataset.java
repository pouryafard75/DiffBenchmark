package benchmark.data.dataset;

import benchmark.data.diffcase.IBenchmarkCase;

import java.nio.file.Path;
import java.util.Set;


/* Created by pourya on 2024-09-28*/
public enum EBenchmarkDataset implements IBenchmarkDataset{
    RefOracle(new RefactoringOracleBenchmarkDataset()),
    Defects4J(new Defects4JBenchmarkDataset()),
    Dummy(new DummyDataset()),
    Experiment(new ExperimentDataset()),
    RefOracleSingleCase(new RefactoringOracleBenchmarkDataset(
            benchmarkCase ->
                benchmarkCase.getCommit().equals("837d1a74bb7d694220644a2539c4440ce55462cf")
            )
    )
    ;

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
