package benchmark.Oracle;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
        BenchmarkMetrics benchmarkMetrics = new BenchmarkMetrics();
        List<DiffFileStats> stats = benchmarkMetrics.generateBenchmarkStats();
        String[] activeTools = benchmarkMetrics.getActiveTools();

        writeStatsToCSV(stats,activeTools);
    }

    private static void writeStatsToCSV(List<DiffFileStats> stats, String[] activeTools) throws IOException {
        FileWriter writer = new FileWriter("stats.csv");
        String[] toolNames = activeTools;
        writer.append("url,srcFileName,ignoredMappings,ignoredElements,");
        for (String toolName : toolNames) {
            toolName = "";
            writer.append(toolName).append("TP_raw,").append(toolName).append("TP,").append(toolName).append("FP,").append(toolName).append("FN,");
            writer.append(toolName).append("TP_raw,").append(toolName).append("TP,").append(toolName).append("FP,").append(toolName).append("FN,");
//            writer.append(toolName).append("_ELEMENTS_TP_ORIGINAL,").append(toolName).append("_ELEMENTS_TP_REFINED,").append(toolName).append("_ELEMENTS_FP,").append(toolName).append("_ELEMENTS_FN,");
        }
        writer.append("\n");
        for (DiffFileStats stat : stats) {
            writer.append(stat.caseInfo.makeURL());
            writer.append(",");
            writer.append(stat.srcFileName);
            writer.append(",");
            writer.append(String.valueOf(stat.getMappings_to_be_ignored()));
            writer.append(",");
            writer.append(String.valueOf(stat.getElements_to_be_ignored()));
            writer.append(",");
            for (String toolName : toolNames) {
                DiffStats tool = stat.diffStatsList.get(toolName);
                writer.append(String.valueOf(tool.getAbstractMappingStats().getTP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getAbstractMappingStats().getTP() - stat.getMappings_to_be_ignored()));
                writer.append(",");
                writer.append(String.valueOf(tool.getAbstractMappingStats().getFP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getAbstractMappingStats().getFN()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getTP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getTP() - stat.getElements_to_be_ignored()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getFP()));
                writer.append(",");
                writer.append(String.valueOf(tool.getProgramElementStats().getFN()));
                writer.append(",");
            }
            writer.append("\n");
        }
        writer.flush();
        writer.close();
    }

}