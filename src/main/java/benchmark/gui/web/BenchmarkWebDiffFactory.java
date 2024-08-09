package benchmark.gui.web;

import benchmark.generators.tools.runners.converter.NoPerfectDiffException;
import benchmark.gui.conf.GuiConf;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;

/* Created by pourya on 2023-04-17 8:58 p.m. */
public class BenchmarkWebDiffFactory {

    private final GuiConf guiConf;
    public BenchmarkWebDiffFactory() {
        guiConf = GuiConf.defaultConf();
    }
    public BenchmarkWebDiffFactory(GuiConf guiConf) {
        this.guiConf = guiConf;
    }
    public BenchmarkWebDiff withURL(String url) throws Exception {
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(ORACLE_DIR));
        return makeDiffs(projectASTDiff,new CaseInfo(url));
    }
    public BenchmarkWebDiff withLocallyClonedRepo(Repository repository, String commit) throws Exception {
        ProjectASTDiff rmDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
        return makeDiffs(rmDiff,null);
    }
    public BenchmarkWebDiff withTwoDirectories(String before, String after, CaseInfo info) throws Exception {
        return makeDiffs(getRMASTDiff(before, after), info);
    }
    public BenchmarkWebDiff withTwoDirectories(String before, String after) throws Exception {
        return makeDiffs(getRMASTDiff(before, after), null);
    }

    private ProjectASTDiff getRMASTDiff(String before, String after) {
        return new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(before), Path.of(after));
    }

    private BenchmarkWebDiff makeDiffs(ProjectASTDiff projectASTDiffByRM, CaseInfo info) throws Exception {
        Configuration conf = null;
        if (info != null && info.getRepo() != null) {
            String repo = info.getRepo();
            conf = (repo.contains(".git")) ? ConfigurationFactory.refOracle() : ConfigurationFactory.defects4j();
        }

        Set<ASTDiff> RM_astDiff = projectASTDiffByRM.getDiffSet();
        Map<ASTDiffTool, Set<ASTDiff>> diffs = new LinkedHashMap<>();

        for (ASTDiff astDiff : RM_astDiff) {
            for (ASTDiffTool tool : guiConf.enabled_tools) {
                diffs.computeIfAbsent(tool, k -> new LinkedHashSet<>());
                try {
                    diffs.get(tool).add(tool.diff(projectASTDiffByRM, astDiff, info, conf));
                }
                catch (NoPerfectDiffException noPerfectDiffException)
                {
                    System.out.println(noPerfectDiffException.getMessage());
                }
                catch (Exception e) {
                    System.out.println("Error in " + tool + " for " + astDiff.getSrcPath() + " and " + astDiff.getDstPath());
                    e.printStackTrace();
                }
            }
        }
        return new BenchmarkWebDiff(projectASTDiffByRM,diffs, guiConf);
    }

}
