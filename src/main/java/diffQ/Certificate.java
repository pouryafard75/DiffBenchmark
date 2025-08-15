package diffQ;

import benchmark.data.diffcase.QCase;
import benchmark.generators.tools.ASTDiffToolEnum;

import java.util.*;

public class Certificate {
    Map<QCase, Map<String, ASTDiffToolEnum>> cle;
    StudyConfig config;
    public Certificate(StudyConfig config) {
        this.config = config;
        this.cle = generateCertificate(config);
    }

    public static Map<QCase, Map<String, ASTDiffToolEnum>> generateCertificate(StudyConfig config) {
        Map<QCase, Map<String, ASTDiffToolEnum>> certificate = new LinkedHashMap<>();
        Random random = new Random();

        for (QCase qCase : config.prs.keySet()) {
            List<ASTDiffToolEnum> toolsForPR = new ArrayList<>(config.tools);

            if (config.randomize) {
                Collections.shuffle(toolsForPR, random);
            }

            Map<String, ASTDiffToolEnum> toolMap = new LinkedHashMap<>();
            for (int i = 0; i < toolsForPR.size(); i++) {
                String alias = config.anonymize ? "Tool" + (i + 1) : toolsForPR.get(i).name();
                toolMap.put(alias, toolsForPR.get(i));
            }
            certificate.put(qCase, toolMap);
        }

        return certificate;
    }
}
