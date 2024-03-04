package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.utils.CaseInfo;
import gui.webdiff.WebDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.IOException;
import java.nio.file.Path;

public class CompareTwoDirectories {
    public static void main(String[] args) throws IOException {
        final String projectRoot = System.getProperty("user.dir");
        String folder1 = projectRoot + "/tmp/v1/";
        String folder2 = projectRoot + "/tmp/v2/";

        folder1 = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/oracle/commits/defects4j/before/Lang/20/";
        folder2 = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/oracle/commits/defects4j/after/Lang/20/";
//
//
//        folder1 = "/Users/pourya/Desktop/tests/Left/";
//        folder2 = "/Users/pourya/Desktop/tests/Right/";

//        CaseInfo info = CaseInfo.fromDirs(folder1, folder2);
        CaseInfo info = new CaseInfo("Lang", "20");

        BenchmarkWebDiff benchmarkWebDiff = null;
        try {
            benchmarkWebDiff = BenchmarkWebDiffFactory.withTwoDirectories(folder1,folder2, info);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        benchmarkWebDiff.run();

    }
}
