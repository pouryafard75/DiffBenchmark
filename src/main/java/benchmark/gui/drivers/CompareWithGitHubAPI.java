package benchmark.gui.drivers;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.exp.ToolSets;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.runners.manipulator.BenchmarkCaseDiffManipulatorImpl;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.models.DiffSide;
import benchmark.models.selector.QueryBySrcPath;
import org.apache.commons.lang3.tuple.Pair;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/eclipse-jgit/jgit/commit/0e73d395061d1bfee365acaa2f79c392175d13bf";

        WebDiffConf webDiffConf = WebDiffConf.defaultConf();
        webDiffConf.setEnabled_tools(ToolSets.MULTIMAPPING_EXTENSION_BATTLE_TOOLS);
//        webDiffConf.setEnabled_tools(Set.of(ASTDiffToolEnum.SPN_FINALIZED));
        BenchmarkWebDiff benchmarkWebDiff = new BenchmarkWebDiffFactory(webDiffConf)

                .withURL(url) /*Recommended for the cases you have url to experiment with*/

//                .withCaseInfo(new D4JCase("Cli", "20")) /*Recommended when you have added the case to benchmark datasets*/

//                .withTwoDirectories("dir1", "dir2") /*Recommended when you have two directories/*
        ;
            benchmarkWebDiff.run();

    }

    private static void addCustomManipulation(WebDiffConf webDiffConf, String url, String filePath) {
        try {
            webDiffConf.addTool(
                    new BenchmarkCaseDiffManipulatorImpl(
                            new GithubCase(url), Pair.of(
                                    new QueryBySrcPath(filePath),
                                    diffManipulator -> {
                                        try {
                                            diffManipulator.acceptAll(ASTDiffToolEnum.RMD);
                                            diffManipulator.discard(DiffSide.LEFT, ASTDiffToolEnum.RMD, 1650, 1680);
                                            diffManipulator.accept(DiffSide.LEFT, ASTDiffToolEnum.GTG, 1650, 1680);
                                        } catch (Exception e) {
                                            throw new RuntimeException(e);
                                        }
                                    }
                            )
                    ));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}