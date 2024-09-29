package benchmark.gui.drivers;

import benchmark.gui.conf.GuiConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";;
//        url = "https://github.com/hazelcast/hazelcast/commit/30c4ae09745d6062077925a54f27205b7401d8df";
//        url = "https://github.com/Alluxio/alluxio/commit/b0938501f1014cf663e33b44ed5bb9b24d19a358";
//        url = "https://github.com/spring-projects/spring-security/commit/fcc9a34356817d93c24b5ccf3107ec234a28b136";
//        url = "https://github.com/assertj/assertj/commit/b36ab386559d04db114db8edd87c8d4cbf850c12";
//        url = "https://github.com/apache/commons-lang/commit/d9a2c69a9d1db6072e1d7b7ea4fcbd5c15d20b5d";
//        url = "https://github.com/deeplearning4j/deeplearning4j/commit/3325f5ccd23f8016fa28a24f878b54f1918546ed";
//        url = "https://github.com/Alluxio/alluxio/commit/5b184ac783784c1ca4baf1437888c79bd9460763";
//        url = "https://github.com/apache/pig/commit/92dce401344a28ff966ad4cf3dd969a676852315";
//        url = "https://github.com/spring-projects/spring-security/commit/fcc9a34356817d93c24b5ccf3107ec234a28b136";
        url = "https://github.com/Alluxio/alluxio/commit/b0938501f1014cf663e33b44ed5bb9b24d19a358";
        url = "https://github.com/openhab/openhab1-addons/commit/f25fa3ae35e4a60a2b7f79a88f14d46ce6cebf55";
        url = "https://github.com/brettwooldridge/HikariCP/commit/2260cc2";
        new BenchmarkWebDiffFactory().withURL(url).run();
    }
}
