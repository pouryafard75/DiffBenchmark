package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url;
        url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/Netflix/eureka/commit/f6212a7e474f812f31ddbce6d4f7a7a0d498b751";
//        url = "https://github.com/real-logic/Aeron/commit/35893c115ba23bd62a7036a33390420f074ce660";
        url = "https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e";
//        url = "https://github.com/rstudio/rstudio/commit/cb49e436b9d7ee55f2531ebc2ef1863f5c9ba9fe";
//        url = "https://github.com/spring-projects/spring-integration/commit/247232bdde24b81814a82100743f77d881aaf06b";
//        url = "https://github.com/JetBrains/intellx   ij-plugins/commit/83b3092c1ee11b70489732f9e69b8e01c2a966f0";
//        url = "https://github.com/eclipse/jetty.project/commit/837d1a74bb7d694220644a2539c4440ce55462cf";
//        url = "https://github.com/eclipse/jetty.project/commit/837d1a74bb7d694220644a2539c4440ce55462cf";
//        url = "https://github.com/koush/AndroidAsync/commit/1bc7905b07821f840068089343e6b77a8686d1ab";
//        url = "https://github.com/liferay/liferay-portal/commit/59fd9e696cec5f2ed44c27422bbc426b11647321";
//        url = "https://github.com/apache/commons-lang/commit/5111ae7db08a70323a51a21df0bbaf46f21e072e";
//        url = "https://github.com/pouryafard75/TestCases/commit/76ab18eeb36f3bc0a8e6a5655d970657187df276";
        url = "https://github.com/apache/cassandra/commit/1a2c1bcdc7267abec9b19d77726aedbb045d79a8";
//        url = "https://github.com/glyptodon/guacamole-client/commit/ce1f3d07976de31aed8f8189ec5e1a6453f4b580";
//        url = "https://github.com/liferay/liferay-portal/commit/59fd9e696cec5f2ed44c27422bbc426b11647321";
//        url = "https://github.com/pouryafard75/TestCases/commit/a0b10eee67fea5705947b89245eeedfc8b6a71ab";
//        url = "https://github.com/CyanogenMod/android_frameworks_base/commit/910397f2390d6821a006991ed6035c76cbc74897";
        url = "https://github.com/koush/AndroidAsync/commit/1bc7905b07821f840068089343e6b77a8686d1ab";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);
        benchmarkWebDiff.run();
    }
}
