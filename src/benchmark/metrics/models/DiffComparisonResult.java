package benchmark.metrics.models;

import benchmark.utils.CaseInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2023-04-03 4:47 a.m. */
public class DiffComparisonResult {
    final CaseInfo caseInfo;
    final String srcFileName;
    final Map<String, DiffStats> diffStatsList = new LinkedHashMap<>();
    private DiffIgnore ignore = null;

    public DiffComparisonResult(CaseInfo caseInfo, String srcFileName) {
        this.caseInfo = caseInfo;
        this.srcFileName = srcFileName;
    }

    public CaseInfo getCaseInfo() {
        return caseInfo;
    }

    public Map<String, DiffStats> getDiffStatsList() {
        return diffStatsList;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    public void setIgnore(DiffIgnore ignore) {
        this.ignore = ignore;
    }

    public DiffIgnore getIgnore() {
        return ignore;
    }

    public void putStats(String toolName, DiffStats diffStats) {
        diffStatsList.put(toolName, diffStats);
    }
}


