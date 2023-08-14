package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.actions.EditScriptGenerator;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.io.File;

import static benchmark.gui.CompareWithLocalDirectories.loadCustomSet;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/raphw/byte-buddy/commit/f1dfb66a368760e77094ac1e3860b332cf0e4eb5";
        url = "https://github.com/pouryafard75/TestCases/commit/562c4447a566170ac28872a88b323669a82db5c9";
        url = "https://github.com/spring-projects/spring-boot/commit/20d39f7af2165c67d5221f556f58820c992d2cc6";
        url = "https://github.com/fabric8io/fabric8/commit/8127b21a220ca677c4e59961d019e7753da7ea6e";
        url = "https://github.com/SonarSource/sonarqube/commit/c55a8c3761e9aae9f375d312c14b1bbb9ee9c0fa";
        url = "https://github.com/WhisperSystems/Signal-Android/commit/f0b2cc559026871c1b4d8e008666afb590553004";
        url = "https://github.com/google/j2objc/commit/fa3e6fa02dadc675f0d487a15cd842b3ac4a0c11";
        url = "https://github.com/zeromq/jeromq/commit/02d3fa171d02c9d82c7bdcaeb739f47d0c0006a0";
        url = "https://github.com/square/javapoet/commit/5a37c2aa596377cb4c9b6f916614407fd0a7d3db";
        url = "https://github.com/JetBrains/intellij-plugins/commit/0df7cb00757fe0d4fac8d8b0d5fc46af95feb238";
        url = "https://github.com/apache/camel/commit/ab1d1dd78fe53edb50c4ede447e4ac5a55ee2ac9";
        url = "https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e";

//        url = "https://github.com/CyanogenMod/android_frameworks_base/commit/910397f2390d6821a006991ed6035c76cbc74897";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);

        benchmarkWebDiff.run();
    }
}
