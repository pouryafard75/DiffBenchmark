package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.actions.EditScriptGenerator;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.io.File;

import static benchmark.gui.CompareWithLocalDirectories.loadCustomSet;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url;
        url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";

//        url = "https://github.com/CyanogenMod/android_frameworks_base/commit/910397f2390d6821a006991ed6035c76cbc74897";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);
        benchmarkWebDiff.run();
    }
}
