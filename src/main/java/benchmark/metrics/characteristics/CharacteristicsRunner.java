package benchmark.metrics.characteristics;

import benchmark.data.dataset.IBenchmarkDataset;

import java.util.EnumSet;
import java.util.Set;

/* Created by pourya on 2023-12-05 9:52 p.m. */
public class CharacteristicsRunner {
    private static final Set<Characteristic> conf = EnumSet.of(
            Characteristic.NUM_OF_CASES,
            Characteristic.NUM_OF_FILES
//            Characteristic.NUM_OF_CASES_WITH_REFACTORINGS,
//            Characteristic.NUM_OF_CASES_WITH_MULTI_MAPPINGS,
//            Characteristic.AVG_CHURN
    );
    public static void main(String[] args) throws Exception {
//        printCharacteristics(ExperimentFactory.refOracle());
//        printCharacteristics(ConfigurationFactory.defects4j());
    }

    private static void printCharacteristics(IBenchmarkDataset benchmarkDataset) {
        for (Characteristic characteristic : CharacteristicsRunner.conf) {
                System.out.println(characteristic + ": " + characteristic.getNumber(benchmarkDataset));
        }
    }
}
