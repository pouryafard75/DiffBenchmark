package benchmark.metrics.models;

import benchmark.data.diffcase.IBenchmarkCase;

import java.io.FileWriter;

/* Created by pourya on 2023-11-29 12:12 a.m. */
public class CommitRefactoringCountComparisonResult extends BaseDiffComparisonResult {
    private final int numOfRefactorings;
    public CommitRefactoringCountComparisonResult(IBenchmarkCase caseInfo, int numOfRefactorings) {
        super(caseInfo);
        this.numOfRefactorings = numOfRefactorings;
    }

    public int getNumOfRefactorings() {
        return numOfRefactorings;
    }

    @Override
    public void writeData(FileWriter writer) throws Exception {
        //todo
    }

    @Override
    public void writeHeader(FileWriter writer) throws Exception {
        //todo
    }
}
