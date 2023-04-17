package benchmark.Oracle;

import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2023-04-03 4:47 a.m. */
public class DiffFileStats {
    final CaseInfo caseInfo;
    final String srcFileName;
    final Map<String,DiffStats> diffStatsList = new LinkedHashMap<>();
    private DiffIgnore ignore = null;

    public DiffFileStats(CaseInfo caseInfo, String srcFileName) {
        this.caseInfo = caseInfo;
        this.srcFileName = srcFileName;
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


