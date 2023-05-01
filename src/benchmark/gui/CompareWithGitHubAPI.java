package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/raphw/byte-buddy/commit/f1dfb66a368760e77094ac1e3860b332cf0e4eb5";
        url = "https://github.com/mongodb/mongo-java-driver/commit/8c5a20d786e66ee4c4b0d743f0f80bf681c419be";
        url = "https://github.com/spring-projects/spring-security/commit/fcc9a34356817d93c24b5ccf3107ec234a28b136";
        url = "https://github.com/jOOQ/jOOQ/commit/227254cf769f3e821ed1b2ef2d88c4ec6b20adea";
        url = "https://github.com/dropwizard/metrics/commit/4c6ab3d77cc67c7a91155d884077520dcf1509c6";
        url = "https://github.com/Atmosphere/atmosphere/commit/69c229b7611ff8c6a20ff2d4da917a68c1cde64a";
        url = "https://github.com/pouryafard75/TestCases/commit/c7f965e7804a01d1b0ea382628db3875254e32f7";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);
        benchmarkWebDiff.run();
    }
}
