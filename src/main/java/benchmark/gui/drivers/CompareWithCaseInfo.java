package benchmark.gui.drivers;

import benchmark.data.dataset.EBenchmarkDataset;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.diffcase.D4JCase;
import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;

/* Created by pourya on 2024-08-09*/
public class CompareWithCaseInfo {
    public static void main(String[] args) {
        IBenchmarkCase info = new D4JCase("Closure", "22");
//        {"repo":"Closure","commit":"22"}]
        info.setDataset(EBenchmarkDataset.Defects4J);
        //Or you can pass it via url as follows:
        //CaseInfo info = new CaseInfo("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825")
        BenchmarkWebDiff benchmarkWebDiff = null;
        try {
            benchmarkWebDiff = new BenchmarkWebDiffFactory().withCaseInfo(info);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        benchmarkWebDiff.run();
    }
}
