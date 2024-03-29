package rq;

/* Created by pourya on 2023-12-04 2:15 p.m. */

import benchmark.metrics.computers.vanilla.CommitPerfectRatioBenchmarkComputer;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.IOException;
import java.util.Map;

/***
 * What's the perfection ratio for each tool
 */
public class RQ7 {

    public void run(Configuration[] conf) {
        if (conf.length > 1) {
            throw new RuntimeException("RQ8 accepts only one configuration");
        }
        Configuration configuration = conf[0];
        try {
            rq8(configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void rq8(Configuration configuration) throws IOException {
        Map<ASTDiffTool, Integer> astDiffToolIntegerMap = new CommitPerfectRatioBenchmarkComputer(configuration).perfectRatio();
        System.out.println(astDiffToolIntegerMap);
    }

    public static void main(String[] args){
        new RQ7().run(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne()});
        new RQ7().run(new Configuration[]{ConfigurationFactory.refOracle()});
        new RQ7().run(new Configuration[]{ConfigurationFactory.defects4jTwoPointOne()});
        new RQ7().run(new Configuration[]{ConfigurationFactory.defects4j()});
    }
}
