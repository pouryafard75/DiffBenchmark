package benchmark.data.dataset;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.diffcase.IBenchmarkCase;

import java.util.Set;

/* Created by pourya on 2024-11-28*/
public class ExperimentDataset extends RefactoringOracleBenchmarkDataset {
    private static final String TEST_URL = "https://github.com/pouryafard75/TestCases/commit/457cf89b923a81aac29f701728ea7e88b3cb87b9";
    @Override
    public Set<? extends IBenchmarkCase> getCases() {
        Set<GithubCase> githubCases = Set.of(
                new GithubCase(TEST_URL)
        );
        for (GithubCase githubCase : githubCases) {
            githubCase.setDataset(this);
        }
        return githubCases;
    }
}