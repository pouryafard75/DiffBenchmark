package benchmark.gui.drivers;

import benchmark.gui.web.BenchmarkWebDiffFactory;

public class CompareWithTwoDirectories {
    public static void main(String[] args) throws Exception {
        String folder1 = "PATH_TO_FOLDER1";
        String folder2 = "PATH_TO_FOLDER2";
        new BenchmarkWebDiffFactory().withTwoDirectories(folder1,folder2).run();
    }
}
