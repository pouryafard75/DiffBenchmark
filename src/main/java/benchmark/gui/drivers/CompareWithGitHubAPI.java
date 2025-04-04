package benchmark.gui.drivers;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.exp.ToolSets;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

import java.util.LinkedHashSet;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
        url = "https://github.com/pouryafard75/TestCases/commit/383a2a200e1272a4171849e813efa3f7287b7024";
        url = "https://github.com/pouryafard75/TestCases/commit/4e31fb03d9e9d67f3b3dd6ea2c1703551deb54a0";
        url = "https://github.com/JetBrains/intellij-community/commit/9fbf6b8";
//        url = "https://github.com/pouryafard75/TestCases/commit/438c21418f843cadc115f9c01eedd28de2ebd280";
//        url = "https://github.com/zeromq/jeromq/commit/02d3fa171d02c9d82c7bdcaeb739f47d0c0006a0";
//        url = "https://github.com/junit-team/junit5/commit/6b575f2ee5f02288a774ff0a85ce3a3e3cb6946f";
//        url = "https://github.com/google/truth/commit/1768840bf1e69892fd2a23776817f620edfed536";
        WebDiffConf webDiffConf = WebDiffConf.defaultConf();
        webDiffConf.setEnabled_tools(ToolSets.ALL);
        webDiffConf.setEnabled_tools(ToolSets.BeforeAndAfterTranslations);
        webDiffConf.setEnabled_tools(ToolSets.VISITOR_EXP_BATTLE_TOOLS);
        webDiffConf.setEnabled_tools(new LinkedHashSet<>()
             {{
//                 this.add(ASTDiffToolEnum.GOD);
//                 this.add(ASTDiffToolEnum.GOD);
//                 this.add(ASTDiffToolEnum.SPN_T);
//                 this.add(ASTDiffToolEnum.SPN_G_);
//                 this.add(ASTDiffToolEnum.SPN_G_T);
//                 this.add(ASTDiffToolEnum.SPN_G_T);
//                 this.add(ASTDiffToolEnum.SPN_S_);
//                 this.add(ASTDiffToolEnum.SPN_S_T);
             }}
        );
        webDiffConf.setEnabled_tools(ToolSets.ALL);
//        webDiffConf.setEnabled_viewers(Set.of(DiffViewers.VANILLA));
        BenchmarkWebDiff benchmarkWebDiff = new BenchmarkWebDiffFactory(webDiffConf)

//                .withURL(url) /*Recommended for the cases you have url to experiment with*/

//                .withCaseInfo(new D4JCase("Cli", "20")) /*Recommended when you have added the case to benchmark datasets*/

//                .withCaseInfo(new GithubCase("https://github.com/BroadleafCommerce/BroadleafCommerce/commit/4ef35268bb96bb78b2dc698fa68e7ce763cde32e")) /*Recommended when you have added the case to benchmark datasets*/
                .withCaseInfo(new GithubCase(url))

//                .withTwoDirectories("dir1", "dir2") /*Recommended when you have two directories/*
        ;
        benchmarkWebDiff.run();
//        writeAll(benchmarkWebDiff);

    }


}