package benchmark.gui.web;

import benchmark.oracle.generators.tools.runners.GT2;
import benchmark.oracle.generators.tools.runners.IJM;
import benchmark.oracle.generators.tools.runners.MTDiff;
import benchmark.oracle.generators.tools.runners.PerfectDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;

import static benchmark.utils.Helpers.diffByGumTree;

/* Created by pourya on 2023-04-17 8:58 p.m. */
public class BenchmarkWebDiffFactory {
    public static BenchmarkWebDiff withURL(String url) throws Exception {
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);
//        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        String REPOS = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/oracle/commits";
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(REPOS));
        return makeDiffs(projectASTDiff,new CaseInfo(repo,commit));
    }
    public static BenchmarkWebDiff withLocallyClonedRepo(String before, String after, CaseInfo info) throws Exception {
        return makeDiffs(getRmAstDiff(before, after), info);
    }

    public static ProjectASTDiff getRmAstDiff(String before, String after) {
        return new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(before), Path.of(after));
    }

    private static BenchmarkWebDiff makeDiffs(ProjectASTDiff projectASTDiffByRM, CaseInfo info) throws Exception {

        Set<ASTDiff> RM_astDiff = projectASTDiffByRM.getDiffSet();
        Set<Diff> GTG_astDiff = new LinkedHashSet<>();
        Set<Diff> GTS_astDiff = new LinkedHashSet<>();
        Set<Diff> IJM_astDiff = new LinkedHashSet<>();
        Set<Diff> MTD_astDiff = new LinkedHashSet<>();
        Set<Diff> GT2_astDiff = new LinkedHashSet<>();
        Set<ASTDiff> GOD_astDiff = new LinkedHashSet<>();
        for (ASTDiff astDiff : RM_astDiff) {
            GTG_astDiff.add(diffByGumTree(astDiff,new CompositeMatchers.ClassicGumtree()));
            GTS_astDiff.add(diffByGumTree(astDiff,new CompositeMatchers.SimpleGumtree()));
            IJM_astDiff.add(new IJM(projectASTDiffByRM,astDiff).diff());
            MTD_astDiff.add(new MTDiff(projectASTDiffByRM,astDiff).diff());
            GT2_astDiff.add(new GT2(projectASTDiffByRM,astDiff).diff());
            ASTDiff perfectDiff;
            try {
                String repo = info.getRepo();
                Configuration conf =  (repo.contains(".git")) ? ConfigurationFactory.refOracle() : ConfigurationFactory.defects4j();

                perfectDiff = new PerfectDiff(projectASTDiffByRM, astDiff, info, conf).makeASTDiff();
                GOD_astDiff.add(perfectDiff);
            }
            catch (Exception e)
            {
                System.out.println("Error in loading perfect diff for " + astDiff.getSrcPath());
            }
        }
        return new BenchmarkWebDiff(projectASTDiffByRM, RM_astDiff, GTG_astDiff, GTS_astDiff, IJM_astDiff, MTD_astDiff, GT2_astDiff, GOD_astDiff);
    }
}
