package benchmark.metrics.computers.violation.writer;

import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.metrics.computers.violation.BenchmarkViolationComputer;
import benchmark.metrics.computers.violation.models.SemanticViolationRecord;
import benchmark.metrics.computers.violation.models.ViolationKind;
import benchmark.metrics.computers.violation.models.ViolationReport;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/* Created by pourya on 2023-12-11 3:26 p.m. */
public class CsvWriter {
    private final BenchmarkViolationComputer benchmarkViolationComputer;

    public CsvWriter(BenchmarkViolationComputer benchmarkViolationComputer)
    {
        this.benchmarkViolationComputer = benchmarkViolationComputer;
    }
    public void writeForAllTools() throws IOException {
        File dir = new File("out/RQ2/");
        if (!dir.exists())
            if (!dir.mkdir()) throw new RuntimeException("Could not create directory" + dir.getAbsolutePath());
        for (ViolationReport report : benchmarkViolationComputer.getReports()) {
            for (Map.Entry<IASTDiffTool, Collection<SemanticViolationRecord>> item : report.getViolations().entrySet()) {
                File destin = dir.toPath().resolve(report.getViolationKind().name()).toFile();
                if (!destin.exists())
                    if (!destin.mkdir())
                        throw new RuntimeException("Could not create directory" + destin.getAbsolutePath());
                IASTDiffTool tool = item.getKey();
                Collection<SemanticViolationRecord> records = item.getValue();
                Path finalPath = destin.toPath().resolve(IExperiment.getMergedNames(benchmarkViolationComputer.getExperiments()) + "-" + tool.getToolName() + ".csv");
                writeToolOutput(records, finalPath.toString());
            }
        }

        Map<IASTDiffTool, Map<ViolationKind, Integer>> mergedViolations = new LinkedHashMap<>();

        for (ViolationReport report : benchmarkViolationComputer.getReports()) {
            Map<IASTDiffTool, Collection<SemanticViolationRecord>> violations = report.getViolations();
            for (Map.Entry<IASTDiffTool, Collection<SemanticViolationRecord>> entry : violations.entrySet()) {
                IASTDiffTool tool = entry.getKey();
                Collection<SemanticViolationRecord> violationRecords = entry.getValue();
                mergedViolations.putIfAbsent(tool, new LinkedHashMap<>());
                Map<ViolationKind, Integer> violationKindIntegerMap = mergedViolations.get(tool);
                violationKindIntegerMap.merge(report.getViolationKind(), violationRecords.size(), Integer::sum);
            }
        }


        writeViolationsToCSV(mergedViolations, "out/rq2-merged.csv");

    }
    private static void writeViolationsToCSV(Map<IASTDiffTool, Map<ViolationKind, Integer>> mergedViolations, String filePath) throws IOException {
        Set<ViolationKind> allViolationTypes = new TreeSet<>();
        for (Map<ViolationKind, Integer> toolViolations : mergedViolations.values()) {
            allViolationTypes.addAll(toolViolations.keySet());
        }

        try (FileWriter writer = new FileWriter(filePath)) {
            // Write header
            writer.append("Tool");
            for (ViolationKind violationType : allViolationTypes) {
                writer.append(",").append(violationType.name());
            }
            writer.append("\n");

            // Write data rows
            for (Map.Entry<IASTDiffTool, Map<ViolationKind, Integer>> entry : mergedViolations.entrySet()) {
                IASTDiffTool tool = entry.getKey();
                Map<ViolationKind, Integer> toolViolations = entry.getValue();

                writer.append(tool.getToolName()); // Assuming ASTDiffTool has a getName method
                for (ViolationKind violationType : allViolationTypes) {
                    writer.append(",");
                    writer.append(String.valueOf(toolViolations.getOrDefault(violationType, 0)));
                }
                writer.append("\n");
            }
        }
    }

    private void writeToolOutput(Collection<SemanticViolationRecord> records, String filePath) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        CSVWriter csvWriter = new CSVWriter(fileWriter);
        String[] header = {"ParentTypesPair", "First", "Second", "URL", "FileName"};
        csvWriter.writeNext(header);
        for (SemanticViolationRecord record : records) {
            String[] data = {
                    record.parentTypesPair(),
                    record.first(),
                    record.second(),
                    record.url(),
                    record.filename()
            };
            csvWriter.writeNext(data);
        }
        csvWriter.close();
    }

}
