package benchmark.gui.drivers;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;

/* Created by pourya on 2024-08-09*/
public class CompareWithCaseInfos {
    public static void main(String[] args) {
        CaseInfo info = new CaseInfo("Lang", "21");
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
