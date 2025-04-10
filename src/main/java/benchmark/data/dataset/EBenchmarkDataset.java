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
//                benchmarkCase.getCommit().equals("f36b736cf1206dd1af794d6fb4cee967a3553b1f")
//                        ||
//                benchmarkCase.getCommit().equals("8d7a26edd1fedb9505b4f2b4fe57b2d2958b4dd9")
//                        ||
                benchmarkCase.getCommit().equals("3224fa8ce7e0079d6ad507e17534cdf01f758876")
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
