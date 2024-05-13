package benchmark.metrics.models;

import benchmark.utils.CaseInfo;

import java.io.FileWriter;
import java.util.Map;

/* Created by pourya on 2023-04-03 4:47 a.m. */
public class FileDiffComparisonResult extends BaseDiffComparisonResult {
    final String srcFileName;
    boolean onFly = false;

    public FileDiffComparisonResult(CaseInfo caseInfo, String srcFileName) {
        super(caseInfo);
        this.srcFileName = srcFileName;
    }
    public FileDiffComparisonResult(CaseInfo caseInfo, String srcFileName, boolean onFly) {
        super(caseInfo);
        this.srcFileName = srcFileName;
        this.onFly = onFly;
    }

    public void setOnFly(boolean onFly) {
        this.onFly = onFly;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    @Override
    public void writeData(FileWriter writer) throws Exception {

        StringBuilder row = new StringBuilder();
            row.append(this.getCaseInfo().makeURL()).append(",")
                    .append(this.getSrcFileName()).append(",");
        writeToolsData(row, onFly);
        row.append("\n");
        writer.append(row.toString());
    }

    @Override
    public void writeHeader(FileWriter writer) throws Exception {
        boolean onFly = false;
        StringBuilder header = new StringBuilder();
        header
                .append("url").append(",")
                .append("srcFileName").append(",");
        writeToolsHeader(header, onFly);
        header.deleteCharAt(header.length() - 1); // Remove trailing comma
        header.append("\n");
        writer.append(header.toString());
    }

    @Override
    public int compareTo(CsvWritable o) {
        if (o instanceof FileDiffComparisonResult){
            int comp = super.compareTo(o);
            if (comp != 0) return comp;
            return this.srcFileName.compareTo(
                    ((FileDiffComparisonResult) o).srcFileName);
        }
        return 0;
    }
}


