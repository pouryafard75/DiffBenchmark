package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.Set;

import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2023-05-02 5:15 p.m. */
public class CompareWithLocalDirectories {
    public static void main(String[] args) throws Exception {
        String projectDir = "Chart";
        String bugID = "1";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withLocallyClonedRepo(getBeforeDir(projectDir, bugID), getAfterDir(projectDir, bugID));
        benchmarkWebDiff.run();
    }
}
