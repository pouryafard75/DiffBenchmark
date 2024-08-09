package benchmark.gui.drivers;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url;
        url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/phishman3579/java-algorithms-implementation/commit/ab98bcacf6e5bf1c3a06f6bcca68f178f880ffc9";
//        url = "https://github.com/brettwooldridge/HikariCP/commit/1571049ec04b1e7e6f082ed5ec071584e7200c12";
//        url = "https://github.com/facebook/buck/commit/84b7b3974ae8171a4de2f804eb94fcd1d6cd6647";


        BenchmarkWebDiff benchmarkWebDiff = new BenchmarkWebDiffFactory().withURL(url);
//        benchmarkWebDiff
        benchmarkWebDiff.run();
    }
}
