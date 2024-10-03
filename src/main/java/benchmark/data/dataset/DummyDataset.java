package benchmark.data.dataset;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.diffcase.GithubCase;

import java.util.Set;

/* Created by pourya on 2024-09-29*/
public class DummyDataset extends RefactoringOracleBenchmarkDataset {
    private static final String TEST_URL = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
    @Override
    public Set<? extends IBenchmarkCase> getCases() {
        return Set.of(new GithubCase(TEST_URL));
    }
}
