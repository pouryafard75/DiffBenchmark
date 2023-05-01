package gui;

import gui.webdiff.WebDiff;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.nio.file.Path;
import java.util.Set;

public class RunWithTwoDirectories {
    public static void main(String[] args) {
        final String projectRoot = System.getProperty("user.dir");
        String folder1 = projectRoot + "/tmp/v1/";
        String folder2 = projectRoot + "/tmp/v2/";

        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(folder1),Path.of(folder2));
        new WebDiff(astDiffs).run();
    }
}
