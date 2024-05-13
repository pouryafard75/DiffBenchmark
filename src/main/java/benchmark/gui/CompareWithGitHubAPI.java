package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url;
        url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/brettwooldridge/HikariCP/commit/1571049ec04b1e7e6f082ed5ec071584e7200c12";
//        url = "https://github.com/Alluxio/alluxio/commit/0ba343846f21649e29ffc600f30a7f3e463fb24c";
        url = "https://github.com/Athou/commafeed/commit/18a7bd1fd1a83b3b8d1b245e32f78c0b4443b7a7";


        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);
//        benchmarkWebDiff
        benchmarkWebDiff.run();
    }
}
