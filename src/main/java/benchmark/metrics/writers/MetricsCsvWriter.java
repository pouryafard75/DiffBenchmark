package benchmark.metrics.writers;

import benchmark.metrics.computers.MappingsToConsider;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.Stats;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.text.SimpleDateFormat;
import java.util.Date;


/* Created by pourya on 2023-11-23 8:47 p.m. */
public class MetricsCsvWriter {
    private final Configuration conf;
    private final Collection<DiffComparisonResult> stats;
    private MappingsToConsider mappingsToConsider;

    public MetricsCsvWriter(Configuration conf, Collection<DiffComparisonResult> stats, MappingsToConsider mappingsToConsider){
        this.conf = conf;
        this.stats = stats;
        this.mappingsToConsider = mappingsToConsider;
    }
    public void writeStatsToCSV(boolean onFly) throws IOException {
        String destination = conf.getOutputFolder() + mappingsToConsider + "-";
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy,HH:mm:ss");
        String timeAndDate = formatter.format(new Date());
        writeStatsToCSV(onFly, destination + timeAndDate);
    }
    public void writeStatsToCSV(boolean onFly, String exportPath) throws IOException {
        ASTDiffTool[] activeTools = conf.getActiveTools();
        try (FileWriter writer = new FileWriter(exportPath)) {
            writeHeader(writer, activeTools, onFly);
            writeData(writer, stats, activeTools, onFly);
        }
    }


    private static void writeHeader(FileWriter writer, ASTDiffTool[] activeTools, boolean onFly) throws IOException {
        String ignoreHeader = "ignoredMappings,ignoredElements,";
        StringBuilder header = new StringBuilder("url,srcFileName," + (onFly ? "" : ignoreHeader));
        for (ASTDiffTool tool : activeTools) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                continue;
            String toolName = tool.name();
            for (String criteria : new String[]{"abstractMapping", "programElement"}) {
                String prefixedToolName = toolName + "_";
                if (onFly) {
                    header
                            .append(prefixedToolName).append("Precision_").append(criteria).append(",")
                            .append(prefixedToolName).append("Recall_").append(criteria).append(",")
                            .append(prefixedToolName).append("Accuracy_").append(criteria).append(",");
                }
                else {
                    header
                            .append(prefixedToolName).append("TP_raw_").append(criteria).append(",")
                            .append(prefixedToolName).append("TP_").append(criteria).append(",")
                            .append(prefixedToolName).append("FP_").append(criteria).append(",")
                            .append(prefixedToolName).append("FN_").append(criteria).append(",");
                }
            }
        }
        header.deleteCharAt(header.length() - 1); // Remove trailing comma
        header.append("\n");
        writer.append(header.toString());
    }
    private static void writeData(FileWriter writer, Collection<DiffComparisonResult> stats, ASTDiffTool[] activeTools, boolean onFly) throws IOException {
        for (DiffComparisonResult stat : stats) {
            StringBuilder row = new StringBuilder();
            row.append(stat.getCaseInfo().makeURL()).append(",")
                    .append(stat.getSrcFileName()).append(",");
            if (!onFly) {
                row
                        .append(stat.getIgnore().intraFileMappings.getMappings().size()).append(",")
                        .append(stat.getIgnore().intraFileMappings.getMatchedElements().size()).append(",");
            }

            for (ASTDiffTool tool : activeTools) {
                if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV))
                    continue;
                String toolName = tool.name();
                DiffStats toolStat = stat.getDiffStatsList().get(toolName);
                appendStats(row, toolStat.getAbstractMappingStats(), stat.getIgnore().intraFileMappings.getMappings().size(),onFly);
                appendStats(row, toolStat.getProgramElementStats(), stat.getIgnore().intraFileMappings.getMatchedElements().size(),onFly);
            }
            row.deleteCharAt(row.length() - 1); // Remove trailing comma
            row.append("\n");
            writer.append(row.toString());
        }
    }


    private static void appendStats(StringBuilder row, Stats stats, int ignore, boolean onFly) {
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
}
