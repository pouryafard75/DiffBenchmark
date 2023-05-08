package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2023-05-02 5:15 p.m. */
public class CompareWithLocalDirectories {
    public static void main(String[] args) throws Exception {
        String projectDir = "Chart";
        String bugID = "6";
        // @Tsantalis This is the list bugIDs which you might want to check: [2,5,6,16,17,22]

        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withLocallyClonedRepo(
                getBeforeDir(projectDir, bugID), getAfterDir(projectDir, bugID));
        benchmarkWebDiff.run();
    }
}
