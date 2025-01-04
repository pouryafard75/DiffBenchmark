package benchmark.gui.web;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.generators.tools.runners.converter.NoPerfectDiffException;
import benchmark.gui.conf.GuiConf;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import static benchmark.conf.Paths.ORACLE_DIR;

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
        return makeDiffs(projectASTDiff,new GithubCase(url));
    }
    public BenchmarkWebDiff withLocallyClonedRepo(Repository repository, String commit) throws Exception {
        ProjectASTDiff rmDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
        return makeDiffs(rmDiff,null);
    }
    public BenchmarkWebDiff withTwoDirectories(String before, String after, IBenchmarkCase info) throws Exception {
        return makeDiffs(getRMASTDiff(before, after), info);
    }
    public BenchmarkWebDiff withTwoDirectories(String before, String after) throws Exception {
        return makeDiffs(getRMASTDiff(before, after), null);
    }
    public BenchmarkWebDiff withCaseInfo(IBenchmarkCase info) throws Exception {
        ProjectASTDiff projectASTDiff = info.getProjectASTDiff();
        return makeDiffs(projectASTDiff,info);
    }


    private ProjectASTDiff getRMASTDiff(String before, String after) {
        return new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(before), Path.of(after));
    }

    private BenchmarkWebDiff makeDiffs(ProjectASTDiff projectASTDiffByRM, IBenchmarkCase info) {
        IExperiment exp = null;
        if (info != null && info.getRepo() != null) {
            String repo = info.getRepo();
            exp = (repo.contains(".git")) ? ExperimentsEnum.REF_EXP_3_0 : ExperimentsEnum.D4J_EXP_3_0;
        }

        Set<ASTDiff> RM_astDiff = projectASTDiffByRM.getDiffSet();
        Map<IASTDiffTool, Set<ASTDiff>> diffs = new LinkedHashMap<>();

        for (ASTDiff astDiff : RM_astDiff) {
            for (ASTDiffToolEnum tool : guiConf.enabled_tools) {
                diffs.computeIfAbsent(tool, k -> new LinkedHashSet<>());
                try {
                    diffs.get(tool).add(tool.diff(info, (x) -> astDiff));
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
