package benchmark.gui.drivers;

import benchmark.data.diffcase.D4JCase;
import benchmark.data.diffcase.GithubCase;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.ToolSets;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2024-08-09*/
public class CompareWithCaseInfo {

    public static final WebDiffConf webDiffConf = WebDiffConf.defaultConf();

    public static void main(String[] args) {
        IBenchmarkCase info = new GithubCase("https://github.com/pouryafard75/TestCases/commit/4e31fb03d9e9d67f3b3dd6ea2c1703551deb54a0");
        info = new D4JCase("Codec", "13");
        webDiffConf.setEnabled_tools(ToolSets.PERFECTION_BATTLE);

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
    }
}
