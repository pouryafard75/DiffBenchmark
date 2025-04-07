package benchmark.data.dataset;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.diffcase.GithubCase;

import java.util.Set;

/* Created by pourya on 2024-09-29*/
public class DummyDataset extends RefactoringOracleBenchmarkDataset {
    private static final String TEST_URL = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
    @Override
    public Set<? extends IBenchmarkCase> getCases() {
        Set<GithubCase> githubCases = Set.of(
                new GithubCase(TEST_URL));
//                new GithubCase("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825"),
//                new GithubCase("https://github.com/CellularPrivacy/Android-IMSI-Catcher-Detector/commit/e235f884f2e0bc258da77b9c80492ad33386fa86"),
//                new GithubCase("https://github.com/crashub/crash/commit/2801269c7e47bd6e243612654a74cee809d20959"),
//                new GithubCase("https://github.com/brettwooldridge/HikariCP/commit/e19c6874431dc2c3046436c2ac249a0ab2ef3457"),
//                new GithubCase("https://github.com/CyanogenMod/android_frameworks_base/commit/96a2c3410f3c71d3ab20857036422f1d64c3a6d3"),
//                new GithubCase("https://github.com/raphw/byte-buddy/commit/f1dfb66a368760e77094ac1e3860b332cf0e4eb5"),
//                new GithubCase("https://github.com/datastax/java-driver/commit/3a0603f8f778be3219a5a0f3a7845cda65f1e172")
//        );
        for (GithubCase githubCase : githubCases) {
            githubCase.setDataset(this);
        }
        return githubCases;
    }
}
