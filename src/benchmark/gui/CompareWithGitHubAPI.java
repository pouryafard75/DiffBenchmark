package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.oracle.generators.changeAPI.IJM;
import benchmark.oracle.generators.changeAPI.MTDiff;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.util.LinkedHashSet;
import java.util.Set;

import static benchmark.utils.Helpers.diffByGumTree;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
        BenchmarkWebDiff benchmarkWebDiff = BenchmarkWebDiffFactory.withURL(url);
        benchmarkWebDiff.run();
    }
}
