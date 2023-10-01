package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url;
        url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e";
//        url = "https://github.com/apache/cassandra/commit/1a2c1bcdc7267abec9b19d77726aedbb045d79a8";
//        url = "https://github.com/Athou/commafeed/commit/18a7bd1fd1a83b3b8d1b245e32f78c0b4443b7a7";
//        url = "https://github.com/libgdx/libgdx/commit/2bd1557bc293cb8c2348374771aad832befbe26f";
//        url = "https://github.com/nutzam/nutz/commit/6599c748ef35d38085703cf3bd41b9b5b6af5f32";
        url = "https://github.com/apache/drill/commit/711992f22ae6d6dfc43bdb4c01bf8f921d175b38";
        url = "https://github.com/Activiti/Activiti/commit/a70ca1d9ad2ea07b19c5e1f9540c809d7a12d3fb";
        url = "https://github.com/JetBrains/intellij-plugins/commit/83b3092c1ee11b70489732f9e69b8e01c2a966f0";
        url = "https://github.com/glyptodon/guacamole-client/commit/ebb483320d971ff4d9e947309668f5da1fcd3d23";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);
//        benchmarkWebDiff
        benchmarkWebDiff.run();
    }
}
