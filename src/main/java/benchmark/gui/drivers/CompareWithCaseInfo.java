package benchmark.gui.drivers;

import benchmark.data.diffcase.D4JCase;
import benchmark.data.diffcase.GithubCase;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.ToolSets;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

import java.io.IOException;
import java.util.Set;

import static benchmark.generators.tools.ASTDiffToolEnum.*;
import static benchmark.generators.tools.runners.Utils.writeAll;

/* Created by pourya on 2024-08-09*/
public class CompareWithCaseInfo {

    public static final WebDiffConf webDiffConf = WebDiffConf.defaultConf();

    public static void main(String[] args) throws IOException {
        IBenchmarkCase info = new GithubCase("https://github.com/pouryafard75/TestCases/commit/4e31fb03d9e9d67f3b3dd6ea2c1703551deb54a0");
        info = new D4JCase("Gson", "9");
        info = new GithubCase("https://github.com/abarisain/dmix/commit/885771d57c97bd2dd48951e8aeaaa87ceb87532b");
        info = new GithubCase("https://github.com/raphw/byte-buddy/commit/f1dfb66a368760e77094ac1e3860b332cf0e4eb5");
        info = new GithubCase("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825");
        info = new GithubCase("https://github.com/checkstyle/checkstyle/commit/5a9b7249e3d092a78ac8e7d48aeeb62bf1c44e20");
        info = new GithubCase("https://github.com/apache/commons-io/commit/57ab49f51f16dadf6ad246dbbcaad18fa3dd7a0e");
        info = new GithubCase("https://github.com/pouryafard75/TestCases/commit/da5f72106aaeb7ec33c5bad3260cc3fde812b9d0");
        info = new D4JCase("JacksonXml", "1");
        info = new GithubCase("https://github.com/SonarSource/sonarqube/commit/7668c875dfa7240b1ec08eb60b42107bae1b4cd3");
        info = new D4JCase("Closure", "148");

        webDiffConf.setEnabled_tools(Set.of(RMD,RMD_FROM_SNAPSHOT, GOD));

//        {"repo":"Closure","commit":"22"}]
        //Or you can pass it via url as follows:
        //CaseInfo info = new CaseInfo("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825")

        BenchmarkWebDiff benchmarkWebDiff = null;
        try {
            benchmarkWebDiff = new BenchmarkWebDiffFactory(webDiffConf).withCaseInfo(info);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        benchmarkWebDiff.run();
//        writeAll(benchmarkWebDiff);
    }

}
