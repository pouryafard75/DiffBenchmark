package benchmark.metrics.models;

import benchmark.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;

import java.io.FileWriter;
import java.util.Objects;

/* Created by pourya on 2023-11-28 11:05 p.m. */
public class RefactoringSpecificComparisonResult extends BaseDiffComparisonResult {
    final Refactoring refactoring;
    final RefactoringType refactoringType;
    private HumanReadableDiff godFinalizedHRD;

    public RefactoringSpecificComparisonResult(CaseInfo caseInfo, Refactoring refactoring) {
        super(caseInfo);
        this.refactoring = refactoring;
        this.refactoringType = refactoring.getRefactoringType();
    }

    public Refactoring getRefactoring() {
        return refactoring;
    }

    public RefactoringType getRefactoringType() {
        return refactoringType;
    }

    public HumanReadableDiff getGodFinalizedHRD() {
        return godFinalizedHRD;
    }

    public void setGodFinalizedHRD(HumanReadableDiff godFinalizedHRD) {
        this.godFinalizedHRD = godFinalizedHRD;
    }

    @Override
    public void writeData(FileWriter writer) throws Exception {
        if (this.getIgnore() == null)
            throw new RuntimeException("Ignore is null");
        StringBuilder row = new StringBuilder();
        row.append(this.getCaseInfo().makeURL()).append(",")
                .append(this.refactoring.toString().replace(","," ")).append(",")
                .append(this.refactoring.getRefactoringType().name())
                .append(",");
        boolean onFly = false;
        writeToolsData(row, onFly);
        row.append("\n");
        writer.append(row.toString());
    }

    @Override
    public void writeHeader(FileWriter writer) throws Exception {
        boolean onFly = false;
        StringBuilder header = new StringBuilder();
        header.append("url").append(",")
                .append("refactoring").append(",")
                .append("type").append(",");
        writeToolsHeader(header, onFly);
        header.deleteCharAt(header.length() - 1); // Remove trailing comma
        header.append("\n");
        writer.append(header.toString());
    }

    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        RefactoringSpecificComparisonResult other = (RefactoringSpecificComparisonResult) obj;

        // Compare the maps for equality using the equals method of Map
        return Objects.equals(this.getDiffStatsList(), other.getDiffStatsList());
    }
}
