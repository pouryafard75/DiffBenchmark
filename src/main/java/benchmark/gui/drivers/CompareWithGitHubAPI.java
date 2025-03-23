package benchmark.gui.drivers;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.exp.ToolSets;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
//        url = "https://github.com/pouryafard75/TestCases/commit/438c21418f843cadc115f9c01eedd28de2ebd280";
        WebDiffConf webDiffConf = WebDiffConf.defaultConf();
        webDiffConf.setEnabled_tools(ToolSets.ALL);
        webDiffConf.setEnabled_tools(ToolSets.BeforeAndAfterTranslations);
        webDiffConf.setEnabled_tools(ToolSets.VISITOR_EXP_BATTLE_TOOLS);
        BenchmarkWebDiff benchmarkWebDiff = new BenchmarkWebDiffFactory(webDiffConf)

//                .withURL(url) /*Recommended for the cases you have url to experiment with*/

//                .withCaseInfo(new D4JCase("Cli", "20")) /*Recommended when you have added the case to benchmark datasets*/

                .withCaseInfo(new GithubCase("https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e")) /*Recommended when you have added the case to benchmark datasets*/

//                .withTwoDirectories("dir1", "dir2") /*Recommended when you have two directories/*
        ;
        benchmarkWebDiff.run();
//        writeAll(benchmarkWebDiff);

    }


}