package benchmark.gui.drivers;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.exp.ToolSets;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

import java.util.LinkedHashSet;

import static benchmark.generators.tools.runners.Utils.writeAll;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825";
        url = "https://github.com/processing/processing/commit/f36b736cf1206dd1af794d6fb4cee967a3553b1f";
        url = "https://github.com/JetBrains/intellij-community/commit/8d7a26edd1fedb9505b4f2b4fe57b2d2958b4dd9";
        url = "https://github.com/google/truth/commit/1768840bf1e69892fd2a23776817f620edfed536";
        url = "https://github.com/phishman3579/java-algorithms-implementation/commit/ab98bcacf6e5bf1c3a06f6bcca68f178f880ffc9";
        url = "https://github.com/fabric8io/fabric8/commit/8127b21a220ca677c4e59961d019e7753da7ea6e";
        url = "https://github.com/eclipse/jetty.project/commit/837d1a74bb7d694220644a2539c4440ce55462cf";
        url = "https://github.com/jetty/jetty.project/commit/837d1a74bb7d694220644a2539c4440ce55462cf";
//        url = "https://github.com/pouryafard75/TestCases/commit/4383fc88c49244e1ac77309075570e6396ee9e70";
        url = "https://github.com/processing/processing/commit/acf67c8cb58d13827e14bbeeec11a66f9277015f";

        WebDiffConf webDiffConf = WebDiffConf.defaultConf();
        webDiffConf.setEnabled_tools(ToolSets.ALL);
        webDiffConf.setEnabled_tools(ToolSets.BeforeAndAfterTranslations);
        webDiffConf.setEnabled_tools(ToolSets.VISITOR_EXP_BATTLE_TOOLS);
        webDiffConf.setEnabled_tools(new LinkedHashSet<>()
             {{
                 this.add(ASTDiffToolEnum.RMD);
//                 this.add(ASTDiffToolEnum.GTG);
//                 this.add(ASTDiffToolEnum.GTS);
                 this.add(ASTDiffToolEnum.SPN_T);
                 this.add(ASTDiffToolEnum.SPN_I);
//                 this.add(ASTDiffToolEnum.SPN_G_T);
//                 this.add(ASTDiffToolEnum.SPN_G_I);
//                 this.add(ASTDiffToolEnum.SPN_S_T);
//                 this.add(ASTDiffToolEnum.SPN_S_I);
             }}
        );

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