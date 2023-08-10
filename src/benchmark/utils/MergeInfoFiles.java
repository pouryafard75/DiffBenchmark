package benchmark.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-07-25 10:23 p.m. */
public class MergeInfoFiles {
    static String parentDir = "snapshots/2023-08-01/defects4J/";
    static String c1 = "cases.json";
    static String c2 = "cases-problematic.json";
    static String out = "merged_cases.json";
    public static void main(String[] args) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        String perfectPath = parentDir + c1;
        String problematicPath = parentDir + c2;
        List<CaseInfo> perfect_infos = mapper.readValue(new File(perfectPath), new TypeReference<>() {});
        List<CaseInfo> problematic_infos = mapper.readValue(new File(problematicPath), new TypeReference<>() {});
        Set<CaseInfo> mergedCases = new HashSet<>();
        mergedCases.addAll(perfect_infos);
        mergedCases.addAll(problematic_infos);
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(parentDir + out), mergedCases);
    }
}
