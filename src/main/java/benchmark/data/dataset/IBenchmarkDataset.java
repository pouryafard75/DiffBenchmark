package benchmark.data.dataset;

import benchmark.data.diffcase.BenchmarkCase;

import java.nio.file.Path;
import java.util.Set;

/* Created by pourya on 2024-09-28*/
public interface IBenchmarkDataset {
    String getDatasetName();
    Path getPerfectDirPath();
    Set<?  extends BenchmarkCase> getCases();
}
