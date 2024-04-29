package benchmark.gui.web;

import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.oracle.generators.tools.runners.*;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashSet;
import java.util.Set;


import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;
import static benchmark.utils.Helpers.diffByGumTree;

/* Created by pourya on 2023-04-17 8:58 p.m. */
public class BenchmarkWebDiffFactory {
    public static BenchmarkWebDiff withURL(String url) throws Exception {
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(ORACLE_DIR));
        return makeDiffs(projectASTDiff,new CaseInfo(url));
    }
    public static BenchmarkWebDiff withLocallyClonedRepo(Repository repository, String commit) throws Exception {
        ProjectASTDiff rmDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
        return makeDiffs(rmDiff,null);
    }
    public static BenchmarkWebDiff withTwoDirectories(String before, String after, CaseInfo info) throws Exception {
        return makeDiffs(getRMASTDiff(before, after), info);
    }
    public static BenchmarkWebDiff withTwoDirectories(String before, String after) throws Exception {
        return makeDiffs(getRMASTDiff(before, after), null);
    }

    private static ProjectASTDiff getRMASTDiff(String before, String after) {
        return new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(before), Path.of(after));
    }

    private static BenchmarkWebDiff makeDiffs(ProjectASTDiff projectASTDiffByRM, CaseInfo info) throws Exception {
        Configuration conf = null;
        if (info != null && info.getRepo() != null) {
            String repo = info.getRepo();
            conf = (repo.contains(".git")) ? ConfigurationFactory.refOracle() : ConfigurationFactory.defects4j();
        }

        Set<ASTDiff> RM_astDiff = projectASTDiffByRM.getDiffSet();
        Set<Diff> GTG_astDiff = new LinkedHashSet<>();
        Set<Diff> GTS_astDiff = new LinkedHashSet<>();
        Set<Diff> IJM_astDiff = new LinkedHashSet<>();
        Set<Diff> MTD_astDiff = new LinkedHashSet<>();
        Set<Diff> GT2_astDiff = new LinkedHashSet<>();
        Set<ASTDiff> GOD_astDiff = new LinkedHashSet<>();
        Set<Diff> iAST_diff = new LinkedHashSet<>();
        Set<ASTDiff> TRV_astDiff = new LinkedHashSet<>();
        Set<ASTDiff> RM2_astDiff = new LinkedHashSet<>();
        Set<ASTDiff> OBV_astDiff  = new LinkedHashSet<>();

        for (ASTDiff astDiff : RM_astDiff) {
            GTG_astDiff.add(diffByGumTree(astDiff,new CompositeMatchers.ClassicGumtree()));
            GTS_astDiff.add(diffByGumTree(astDiff, new CompositeMatchers.SimpleGumtree()));
            IJM_astDiff.add(new IJM(projectASTDiffByRM,astDiff).diff());
            MTD_astDiff.add(new MTDiff(projectASTDiffByRM,astDiff, info, conf).diff());
            GT2_astDiff.add(new GT2(projectASTDiffByRM,astDiff, info, conf).diff());
            iAST_diff.add(new iASTMapper(projectASTDiffByRM,astDiff).diff());
            if (info != null) {
                try {
                    GOD_astDiff.add(new PerfectDiff(projectASTDiffByRM, astDiff, info, conf).makeASTDiff());
                    RM2_astDiff.add(new RM2(projectASTDiffByRM, astDiff, info, conf).makeASTDiff());
                    TRV_astDiff.add(new TrivialDiff(projectASTDiffByRM, astDiff, info, conf).makeASTDiff());
                    OBV_astDiff.add(ASTDiffTool.OBV.getFactory().getASTDiff(projectASTDiffByRM, astDiff, info, conf));
                } catch (Exception e) {
                    System.out.println("Error in loading perfect diff for " + astDiff.getSrcPath());
                }
            }
        }
        return new BenchmarkWebDiff(projectASTDiffByRM,
                RM_astDiff,
                GTG_astDiff,
                GTS_astDiff,
                IJM_astDiff,
                MTD_astDiff,
                GT2_astDiff,
                iAST_diff,
                RM2_astDiff,
                TRV_astDiff,
                OBV_astDiff,
                GOD_astDiff);
    }

}
