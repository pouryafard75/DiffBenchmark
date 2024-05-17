package rq;

/* Created by pourya on 2023-12-04 2:15 p.m. */

import benchmark.metrics.computers.vanilla.CommitPerfectRatioBenchmarkComputer;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * What's the perfection ratio for each tool
 */
public class RQ7 implements RQ  {
    @Override
    public void run(Configuration[] conf) {
        try {
            rq7(conf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void rq7(Configuration[] confs) throws IOException {
        for (Configuration configuration : confs) {
            Map<ASTDiffTool, Integer> astDiffToolIntegerMap = new CommitPerfectRatioBenchmarkComputer(configuration).perfectRatio();
            RQ.writeToFile(astDiffToolIntegerMap, "out/rq7-" + configuration.getName() + ".csv");
        }
    }
}
