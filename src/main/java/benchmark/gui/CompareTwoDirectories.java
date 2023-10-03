package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

import java.io.IOException;

public class CompareTwoDirectories {
    public static void main(String[] args) throws IOException {
        final String projectRoot = System.getProperty("user.dir");
        String folder1 = projectRoot + "/tmp/v1/";
        String folder2 = projectRoot + "/tmp/v2/";


        BenchmarkWebDiff benchmarkWebDiff = null;
        try {
            benchmarkWebDiff = BenchmarkWebDiffFactory.withTwoDirectories(folder1,folder2);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        benchmarkWebDiff.run();

    }
}
