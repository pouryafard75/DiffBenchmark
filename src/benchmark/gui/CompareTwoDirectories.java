package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.utils.CaseInfo;

import java.io.IOException;

import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

public class CompareTwoDirectories {
    public static void main(String[] args) throws IOException {
        final String projectRoot = System.getProperty("user.dir");
//        String folder1 = projectRoot + "/tmp/v1/";
//        String folder2 = projectRoot + "/tmp/v2/";
//
//
        String projectDir = "Closure";
        String bugID = "7";

        BenchmarkWebDiff benchmarkWebDiff = null;
        try {
            benchmarkWebDiff = BenchmarkWebDiffFactory.withLocallyClonedRepo(
                    getBeforeDir(projectDir, bugID), getAfterDir(projectDir, bugID),new CaseInfo(projectDir, bugID));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        benchmarkWebDiff.run();

    }
}

//Add constraint for composite mapper : if the output is only one SimpleName, discard it
