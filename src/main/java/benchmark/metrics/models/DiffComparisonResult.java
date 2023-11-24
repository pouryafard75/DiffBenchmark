package benchmark.metrics.models;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import benchmark.utils.CaseInfo;

import java.util.*;

/* Created by pourya on 2023-04-03 4:47 a.m. */
public class DiffComparisonResult {
    final CaseInfo caseInfo;
    final String srcFileName;
    final Map<String, DiffStats> diffStatsList = new LinkedHashMap<>();
    private HumanReadableDiff ignore = null;

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

    public void setIgnore(HumanReadableDiff ignore) {
        this.ignore = ignore;
    }

    public HumanReadableDiff getIgnore() {
        return ignore;
    }

    public void putStats(String toolName, DiffStats diffStats) {
        diffStatsList.put(toolName, diffStats);
    }


    public void addToIgnore(NecessaryMappings trivial) {
        List<AbstractMapping> newElements = new ArrayList<>(ignore.intraFileMappings.getMatchedElements());
        newElements.addAll(trivial.getMatchedElements());

        List<AbstractMapping> newMappings = new ArrayList<>(ignore.intraFileMappings.getMappings());
        newMappings.addAll(trivial.getMappings());
        ignore = new HumanReadableDiff(new NecessaryMappings(newElements ,newMappings));
    }
}


