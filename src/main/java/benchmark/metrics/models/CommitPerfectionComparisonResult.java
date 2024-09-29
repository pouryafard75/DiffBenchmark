package benchmark.metrics.models;

import benchmark.data.diffcase.BenchmarkCase;

import java.io.FileWriter;

/* Created by pourya on 2023-12-04 2:23 p.m. */
public class CommitPerfectionComparisonResult extends BaseDiffComparisonResult {

    CommitPerfectionComparisonResult(BenchmarkCase caseInfo) {
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
