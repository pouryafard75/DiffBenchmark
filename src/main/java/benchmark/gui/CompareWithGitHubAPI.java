package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url;
//        url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
        url = "https://github.com/languagetool-org/languagetool/commit/01cddc5afb590b4d36cb784637a8ea8aa31d3561";
        url = "https://github.com/gradle/gradle/commit/b1fb1192daa1647b0bd525600dd41063765eca70";
        url = "https://github.com/google/guava/commit/5bab9e837cf273250aa26702204f139fdcfd9e7a";
        url = "https://github.com/phishman3579/java-algorithms-implementation/commit/ab98bcacf6e5bf1c3a06f6bcca68f178f880ffc9";
        url = "https://github.com/google/truth/commit/1768840bf1e69892fd2a23776817f620edfed536";
        url = "https://github.com/eclipse/jetty.project/commit/837d1a74bb7d694220644a2539c4440ce55462cf";
        url = "https://github.com/spring-projects/spring-boot/commit/cb98ee25ff52bf97faebe3f45cdef0ced9b4416e";
//        url = "https://github.com/JetBrains/intellij-community/commit/138911ce88b05039242b8d1b2bb5b7a59008f5ee";
//        url = "https://github.com/CyanogenMod/android_frameworks_base/commit/658a918eebcbdeb4f920c2947ca8d0e79ad86d89";


        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);
//        benchmarkWebDiff
        benchmarkWebDiff.run();
    }
}
