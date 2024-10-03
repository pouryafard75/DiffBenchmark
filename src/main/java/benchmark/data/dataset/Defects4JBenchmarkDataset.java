package benchmark.data.dataset;


import benchmark.data.diffcase.AbstractIBenchmarkCase;
import benchmark.data.diffcase.D4JCase;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.Set;

import static benchmark.conf.Paths.FINALIZED_REFACTORING_MINER_PATH;

/* Created by pourya on 2024-09-28*/
public class Defects4JBenchmarkDataset extends InspiredFromRMinerTestsBenchmarkDataset {
    private static final String DEFECTS4J_MAPPING_DIR = FINALIZED_REFACTORING_MINER_PATH + "/src/test/resources/astDiff/defects4j/";

    public Defects4JBenchmarkDataset() {
        super(DEFECTS4J_MAPPING_DIR);
    }

    @Override
    public String getDatasetName() {
        return "Defects4J";
    }

    @Override
    public TypeReference<? extends Set<? extends AbstractIBenchmarkCase>> getTypeReference() {
        return new TypeReference<Set<D4JCase>>() {};
    }
}
