package benchmark.gui.drivers;

import benchmark.data.diffcase.D4JCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import com.github.gumtreediff.matchers.Mapping;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/pouryafard75/TestCases/commit/418f51d9fcc65807208e32801b94901400544c38";
//        url = "https://github.com/pouryafard75/TestCases/commit/457cf89b923a81aac29f701728ea7e88b3cb87b9";
//        url = "https://github.com/Activiti/Activiti/commit/a70ca1d9ad2ea07b19c5e1f9540c809d7a12d3fb";
//        url = "https://github.com/pouryafard75/TestCases/commit/ac1ccac6c1bff176573d3d71d6b12e6c4301acb9";
//        url = "https://github.com/CyanogenMod/android_frameworks_base/commit/658a918eebcbdeb4f920c2947ca8d0e79ad86d89";
//        url = "https://github.com/bazelbuild/bazel/commit/91673a2c11d64b1ec21bf1edfe35a242a7daa569";
//        url = "https://github.com/pouryafard75/TestCases/commit/418f51d9fcc65807208e32801b94901400544c38";
//        url = "https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e";
//        url = "https://github.com/pouryafard75/TestCases/commit/4e31fb03d9e9d67f3b3dd6ea2c1703551deb54a0";
        url = "https://github.com/gradle/gradle/commit/3a7ccf5a252077332b9505acb22f190745f726f7";
        url = "https://github.com/glyptodon/guacamole-client/commit/ce1f3d07976de31aed8f8189ec5e1a6453f4b580";
        url = "https://github.com/apache/hive/commit/4ccc0c37aabbd90ecaa36fcc491e2270e7e9bea6";
        url = "https://github.com/google/truth/commit/1768840bf1e69892fd2a23776817f620edfed536";

        BenchmarkWebDiff benchmarkWebDiff = new BenchmarkWebDiffFactory().withCaseInfo(
                new D4JCase("Cli", "20")
        );

//        extracted(ASTDiffToolEnum.SPN, benchmarkWebDiff.diffs);
//        extracted(ASTDiffToolEnum.SPN_COMP, benchmarkWebDiff.diffs);
//        extracted(ASTDiffToolEnum.RMD, benchmarkWebDiff.diffs);
        benchmarkWebDiff.run();

    }

    private static void extracted(ASTDiffToolEnum tool, Map<IASTDiffTool, Set<ASTDiff>> diffs) throws IOException {
        ASTDiff next = diffs.get(tool).iterator().next();
        String folder = "debug/";
        FileUtils.write(new File(folder + "src_" + tool.name() + ".txt"), next.src.getRoot().toTreeString());
        FileUtils.write(new File(folder + "dst_" + tool.name() + ".txt"), next.dst.getRoot().toTreeString());
        Set<Mapping> mappings = next.getAllMappings().getMappings();
        StringBuilder ms = new StringBuilder();
        for (Mapping mapping : mappings) {
            ms.append(mapping.first).append(" -> ").append(mapping.second).append("\n");
        }
        FileUtils.write(new File(folder + "mappings_" + tool.name() + ".txt"), ms);
    }
}