package benchmark.metrics.computers.violation.writer;

import benchmark.metrics.computers.violation.BenchmarkViolationComputer;
import benchmark.metrics.computers.violation.models.SemanticViolationRecord;
import benchmark.metrics.computers.violation.models.ViolationReport;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

/* Created by pourya on 2023-12-11 3:26 p.m. */
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
            for (Map.Entry<ASTDiffTool, Collection<SemanticViolationRecord>> item : report.getViolations().entrySet()) {
                File destin = dir.toPath().resolve(report.getViolationKind().name()).toFile();
                if (!destin.exists())
                    if (!destin.mkdir())
                        throw new RuntimeException("Could not create directory" + destin.getAbsolutePath());
                ASTDiffTool tool = item.getKey();
                Collection<SemanticViolationRecord> records = item.getValue();
                Path finalPath = destin.toPath().resolve(Configuration.geMergedNames(benchmarkViolationComputer.getConfigurations()) + "-" + tool.name() + ".csv");
                writeToolOutput(records, finalPath.toString());
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
