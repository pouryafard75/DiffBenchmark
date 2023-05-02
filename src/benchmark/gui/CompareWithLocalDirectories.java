package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

import java.nio.file.Path;

import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2023-05-02 5:15 p.m. */
public class CompareWithLocalDirectories {
    public static void main(String[] args) throws Exception {
        String projectDir = "Chart";
        String bugID = "2";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withLocallyClonedRepo(getBeforeDir(projectDir, bugID), getAfterDir(projectDir, bugID));
        benchmarkWebDiff.run();
    }
}
