package benchmark.metrics.characteristics;

import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

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
        printCharacteristics(ConfigurationFactory.refOracle());
//        printCharacteristics(ConfigurationFactory.defects4j());
    }

    private static void printCharacteristics(Configuration configuration) {
        for (Characteristic characteristic : CharacteristicsRunner.conf) {
                System.out.println(characteristic + ": " + characteristic.getNumber(configuration));
        }
    }
}
