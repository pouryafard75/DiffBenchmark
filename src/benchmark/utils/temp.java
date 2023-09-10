package benchmark.utils;

import java.util.ArrayList;
import java.util.Set;

/* Created by pourya on 2023-08-31 2:47 a.m. */
public class temp {
    public static void main(String[] args) throws Exception {
        Set<CaseInfo> infos = Configuration.ConfigurationFactory.getDefault().getAllCases();
        ArrayList<String> lines = new ArrayList<>();
        int i = 0;
        String delimiter = "\t";
        for (CaseInfo info : infos) {
            System.out.println(info.getRepo() + "\t" + info.getCommit());
        }
    }

}
