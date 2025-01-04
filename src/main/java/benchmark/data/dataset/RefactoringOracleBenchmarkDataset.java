package benchmark.data.dataset;

import benchmark.data.diffcase.AbstractIBenchmarkCase;
import benchmark.data.diffcase.GithubCase;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;

import static benchmark.conf.Paths.FINALIZED_REFACTORING_MINER_PATH;

/* Created by pourya on 2024-09-28*/
public class RefactoringOracleBenchmarkDataset extends InspiredFromRMinerTestsBenchmarkDataset {
    private static final String REFACTORING_MAPPINGS_DIR = FINALIZED_REFACTORING_MINER_PATH + "/src/test/resources/astDiff/commits/";

    public RefactoringOracleBenchmarkDataset() {
        super(REFACTORING_MAPPINGS_DIR);
    }

    @Override
    public String getDatasetName() {
        return "RefOracle";
    }

    @Override
    public TypeReference<? extends Set<? extends AbstractIBenchmarkCase>> getTypeReference() {
        return new TypeReference<Set<GithubCase>>() {};
    }
}
