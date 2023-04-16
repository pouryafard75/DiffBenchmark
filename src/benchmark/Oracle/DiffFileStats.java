package benchmark.Oracle;

import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2023-04-03 4:47 a.m. */
public class DiffFileStats {
    final CaseInfo caseInfo;
    final String srcFileName;
    final Map<String,DiffStats> diffStatsList = new LinkedHashMap<>();

    public int getElements_to_be_ignored() {
        return elements_to_be_ignored;
    }

    public void setElements_to_be_ignored(int elements_to_be_ignored) {
        this.elements_to_be_ignored = elements_to_be_ignored;
    }

    public int getMappings_to_be_ignored() {
        return mappings_to_be_ignored;
    }

    public void setMappings_to_be_ignored(int mappings_to_be_ignored) {
        this.mappings_to_be_ignored = mappings_to_be_ignored;
    }

    private int elements_to_be_ignored;
    private int mappings_to_be_ignored;

    public DiffFileStats(CaseInfo caseInfo, String srcFileName) {
        this.caseInfo = caseInfo;
        this.srcFileName = srcFileName;
    }
    public void putStats(String toolName, DiffStats diffStats) {
        diffStatsList.put(toolName, diffStats);
    }
}


