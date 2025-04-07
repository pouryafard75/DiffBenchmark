package benchmark.data.dataset;

import benchmark.data.diffcase.IBenchmarkCase;

import java.nio.file.Path;
import java.util.Set;

/* Created by pourya on 2024-09-28*/
public interface IBenchmarkDataset {
    /**
     * Get the name of the dataset
     */
    String getDatasetName();

    /**
     * Get the path to the dataset perfect diffs (ground truth)
     */
    Path getPerfectDirPath();

    /**
     * Get the set of benchmark cases
     */
    Set<?  extends IBenchmarkCase> getCases();
}
