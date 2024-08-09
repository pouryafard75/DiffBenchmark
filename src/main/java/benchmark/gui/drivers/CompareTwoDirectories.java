package benchmark.gui.drivers;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.utils.CaseInfo;

import java.io.IOException;

public class CompareTwoDirectories {
    public static void main(String[] args) throws IOException {
        final String projectRoot = System.getProperty("user.dir");
        String folder1 = projectRoot + "/tmp/v1/";
        String folder2 = projectRoot + "/tmp/v2/";

        folder1 = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/oracle/commits/defects4j/before/Lang/20/";
        folder2 = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/oracle/commits/defects4j/after/Lang/20/";

        CaseInfo info = new CaseInfo("Lang", "20");

        BenchmarkWebDiff benchmarkWebDiff = null;
        try {
            benchmarkWebDiff = new BenchmarkWebDiffFactory().withTwoDirectories(folder1,folder2, info);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        benchmarkWebDiff.run();

    }
}
