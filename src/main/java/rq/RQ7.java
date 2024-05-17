package rq;

/* Created by pourya on 2023-12-04 2:15 p.m. */

import benchmark.metrics.computers.vanilla.CommitPerfectRatioBenchmarkComputer;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.IOException;
import java.util.Map;

/***
 * What's the perfection ratio for each tool
 */
public class RQ7 implements RQ  {
    @Override
    public void run(Configuration[] conf) {
        if (conf.length > 1) {
            throw new RuntimeException("RQ7 accepts only one configuration");
        }
        Configuration configuration = conf[0];
        try {
            rq7(configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void rq7(Configuration configuration) throws IOException {
        Map<ASTDiffTool, Integer> astDiffToolIntegerMap = new CommitPerfectRatioBenchmarkComputer(configuration).perfectRatio();
        System.out.println(astDiffToolIntegerMap);
    }
}
