package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
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
        String url = "https://github.com/spring-projects/spring-boot/commit/cb98ee25ff52bf97faebe3f45cdef0ced9b4416e";
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);
        Set<ASTDiff> RM_astDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        Set<Diff> GTG_astDiff = new LinkedHashSet<>();
        Set<Diff> GTS_astDiff = new LinkedHashSet<>();
        Set<Diff> IJM_astDiff = new LinkedHashSet<>();
        Set<Diff> MTD_astDiff = new LinkedHashSet<>();
        for (ASTDiff astDiff : RM_astDiff) {
            GTG_astDiff.add(diffByGumTree(astDiff,new CompositeMatchers.ClassicGumtree()));
            GTS_astDiff.add(diffByGumTree(astDiff,new CompositeMatchers.SimpleGumtree()));
//            TODO: Fix the issue with IJM & MTDiff
//            IJM_astDiff.add(new IJM(astDiff).diff());
//            MTD_astDiff.add(new MTDiff(astDiff).diff());
        }
        new BenchmarkWebDiff(RM_astDiff, GTG_astDiff, GTS_astDiff, IJM_astDiff, MTD_astDiff).run();

    }
}
