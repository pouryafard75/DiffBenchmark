package benchmark.metrics.models;

import benchmark.utils.CaseInfo;

import java.io.FileWriter;
import java.util.Map;

/* Created by pourya on 2023-04-03 4:47 a.m. */
public class FileDiffComparisonResult extends BaseDiffComparisonResult {
    final String srcFileName;

    public FileDiffComparisonResult(CaseInfo caseInfo, String srcFileName) {
        super(caseInfo);
        this.srcFileName = srcFileName;
    }

    public String getSrcFileName() {
        return srcFileName;
    }

    @Override
    public void writeData(FileWriter writer) throws Exception {

        StringBuilder row = new StringBuilder();
            row.append(this.getCaseInfo().makeURL()).append(",")
                    .append(this.getSrcFileName()).append(",");
        boolean onFly = false;
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
}


