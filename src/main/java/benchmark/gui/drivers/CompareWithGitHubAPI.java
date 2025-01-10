package benchmark.gui.drivers;

import benchmark.data.diffcase.GithubCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.manupilator.BenchmarkCaseDiffManipulatorImpl;
import benchmark.manupilator.DiffSide;
import benchmark.utils.Experiments.QueryBySrcPath;
import org.apache.commons.lang3.tuple.Pair;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/pouryafard75/TestCases/commit/4e31fb03d9e9d67f3b3dd6ea2c1703551deb54a0";
        String filePath = "servers/src/main/java/tachyon/worker/block/allocator/MaxFreeAllocator.java";
        WebDiffConf webDiffConf = WebDiffConf.defaultConf();
        addCustomManipulation(webDiffConf, url, filePath);
        BenchmarkWebDiff benchmarkWebDiff = new BenchmarkWebDiffFactory(webDiffConf)
                .withURL(url) /*Recommended for the cases you have a url to experiment with*/

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