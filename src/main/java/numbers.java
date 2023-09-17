import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;

import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-09-15 2:03 p.m. */
public class numbers {
    public static void main(String[] args) throws IOException {
        Set<CaseInfo> allCases = Configuration.ConfigurationFactory.refOracle().getAllCases();
        System.out.println(allCases.size());
    }
}
