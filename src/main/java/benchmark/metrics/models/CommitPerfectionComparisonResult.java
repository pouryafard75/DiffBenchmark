package benchmark.metrics.models;

import benchmark.data.diffcase.IBenchmarkCase;

import java.io.FileWriter;

/* Created by pourya on 2023-12-04 2:23 p.m. */
public class CommitPerfectionComparisonResult extends BaseDiffComparisonResult {

    CommitPerfectionComparisonResult(IBenchmarkCase caseInfo) {
        super(caseInfo);
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
