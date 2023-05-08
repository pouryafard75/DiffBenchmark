package benchmark.gui.web;

import benchmark.oracle.generators.APIChangerException;
import benchmark.oracle.generators.changeAPI.IJM;
import benchmark.oracle.generators.changeAPI.MTDiff;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import static benchmark.utils.Helpers.diffByGumTree;

/* Created by pourya on 2023-04-17 8:58 p.m. */
public class BenchmarkWebDiffFactory {
    public static BenchmarkWebDiff withURL(String url) throws IOException, APIChangerException {
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
            IJM_astDiff.add(new IJM(astDiff).diff());
            MTD_astDiff.add(new MTDiff(astDiff).diff());
        }
        return new BenchmarkWebDiff(RM_astDiff, GTG_astDiff, GTS_astDiff, IJM_astDiff, MTD_astDiff);
    }
    public static BenchmarkWebDiff withLocallyClonedRepo(String before, String after) throws IOException, APIChangerException {
        Set<ASTDiff> RM_astDiff = new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(before), Path.of(after));
        Set<Diff> GTG_astDiff = new LinkedHashSet<>();
        Set<Diff> GTS_astDiff = new LinkedHashSet<>();
        Set<Diff> IJM_astDiff = new LinkedHashSet<>();
        Set<Diff> MTD_astDiff = new LinkedHashSet<>();
        for (ASTDiff astDiff : RM_astDiff) {
            GTG_astDiff.add(diffByGumTree(astDiff,new CompositeMatchers.ClassicGumtree()));
            GTS_astDiff.add(diffByGumTree(astDiff,new CompositeMatchers.SimpleGumtree()));
//            TODO: Fix the issue with IJM & MTDiff
            IJM_astDiff.add(new IJM(astDiff).diff());
            MTD_astDiff.add(new MTDiff(astDiff).diff());
        }
        return new BenchmarkWebDiff(RM_astDiff, GTG_astDiff, GTS_astDiff, IJM_astDiff, MTD_astDiff);
    }
}
