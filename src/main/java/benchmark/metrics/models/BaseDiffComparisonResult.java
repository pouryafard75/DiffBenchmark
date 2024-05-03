package benchmark.metrics.models;

import benchmark.models.AbstractMapping;
import benchmark.models.HumanReadableDiff;
import benchmark.models.NecessaryMappings;
import benchmark.utils.CaseInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/* Created by pourya on 2023-11-28 10:56 p.m. */
public abstract class BaseDiffComparisonResult implements CsvWritable{
    final Map<String, DiffStats> diffStatsList = new LinkedHashMap<>();
    final CaseInfo caseInfo;
    protected HumanReadableDiff ignore = null;

    public Map<String, DiffStats> getDiffStatsList() {
        return diffStatsList;
    }

    BaseDiffComparisonResult(CaseInfo caseInfo) {
        this.caseInfo = caseInfo;
    }

    public void putStats(String toolName, DiffStats diffStats) {
        diffStatsList.put(toolName, diffStats);
    }

    public CaseInfo getCaseInfo() {
        return caseInfo;
    }

    public void setIgnore(HumanReadableDiff ignore) {
        this.ignore = ignore;
    }

    public HumanReadableDiff getIgnore() {
        return ignore;
    }

    public void addToIgnore(NecessaryMappings trivial) {
        List<AbstractMapping> newElements = new ArrayList<>(getIgnore().getIntraFileMappings().getMatchedElements());
        newElements.addAll(trivial.getMatchedElements());

        List<AbstractMapping> newMappings = new ArrayList<>(getIgnore().getIntraFileMappings().getMappings());
        newMappings.addAll(trivial.getMappings());
        setIgnore(new HumanReadableDiff(new NecessaryMappings(newElements ,newMappings)));
    }

    void writeToolsData(StringBuilder row, boolean onFly) {
        if (!onFly) {
            row
                    .append(this.getIgnore().getIntraFileMappings().getMappings().size()).append(",")
                    .append(this.getIgnore().getIntraFileMappings().getMatchedElements().size()).append(",");
        }
        for (Map.Entry<String, DiffStats> entry : this.getDiffStatsList().entrySet()) {
            DiffStats toolStat = entry.getValue();
            appendStats(row, toolStat.getAbstractMappingStats(), this.getIgnore().getIntraFileMappings().getMappings().size(),onFly);
            appendStats(row, toolStat.getProgramElementStats(), this.getIgnore().getIntraFileMappings().getMatchedElements().size(),onFly);
        }
        row.deleteCharAt(row.length() - 1); // Remove trailing comma
    }

    void writeToolsHeader(StringBuilder header, boolean onFly) {
        String ignoreHeader = "ignoredMappings,ignoredElements,";
        header.append((onFly ? "" : ignoreHeader));
        for (Map.Entry<String, DiffStats> entry : getDiffStatsList().entrySet()) {
            String toolName = entry.getKey();
            for (String criteria : new String[]{"abstractMapping", "programElement"}) {
                String prefixedToolName = toolName + "_";
                if (onFly) {
                    header
                            .append(prefixedToolName).append("Precision_").append(criteria).append(",")
                            .append(prefixedToolName).append("Recall_").append(criteria).append(",")
                            .append(prefixedToolName).append("Accuracy_").append(criteria).append(",");
                } else {
                    header
                            .append(prefixedToolName).append("TP_raw_").append(criteria).append(",")
                            .append(prefixedToolName).append("TP_").append(criteria).append(",")
                            .append(prefixedToolName).append("FP_").append(criteria).append(",")
                            .append(prefixedToolName).append("FN_").append(criteria).append(",");
                }
            }
        }
    }
    public void appendStats(StringBuilder row, Stats stats, int ignore, boolean onFly) {
        if (onFly) {
            Stats finalStats = new Stats(stats.getTP() - ignore, stats.getFP(), stats.getFN());
            row
                    .append(finalStats.calcPrecision()).append(",")
                    .append(finalStats.calcRecall()).append(",")
                    .append(finalStats.calcAccuracy()).append(",");
        }
        else {
            row
                    .append(stats.getTP()).append(",")
                    .append(stats.getTP() - ignore).append(",")
                    .append(stats.getFP()).append(",")
                    .append(stats.getFN()).append(",");
        }
    }

    @Override
    public int compareTo(CsvWritable o) {
        if (o instanceof BaseDiffComparisonResult)
        {
            BaseDiffComparisonResult o2 = (BaseDiffComparisonResult) (o);
            return this.caseInfo.makeURL().compareTo(o2.caseInfo.makeURL());
        }
        else {
            return 0;
        }
    }

}
