package benchmark.gui.drivers;

import benchmark.data.diffcase.D4JCase;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.ToolSets;
import benchmark.gui.conf.WebDiffConf;
import benchmark.gui.viewers.DiffViewers;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

import java.util.Set;

/* Created by pourya on 2024-08-09*/
public class CompareWithCaseInfo {

    public static final WebDiffConf webDiffConf = WebDiffConf.defaultConf();

    public static void main(String[] args) {
        IBenchmarkCase info = new D4JCase("Closure", "22");
        webDiffConf.setEnabled_tools(ToolSets.LITERATURE_TOOLS);
        webDiffConf.setEnabled_viewers(Set.of(DiffViewers.VANILLA));

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
