package benchmark.data.dataset;

import benchmark.data.diffcase.IBenchmarkCase;

import java.util.Set;

public class RefactoringOracleMiscBenchmarkDatasetImpl extends RefactoringOracleBenchmarkDataset {
    String miscInfoName = "cases-miscellaneous.json";
    @Override
    public Set<? extends IBenchmarkCase> getCases() {
        return makeAllCases(getTypeReference(),
                getPerfectDirPath().resolve(miscInfoName)
        );
    }
}
