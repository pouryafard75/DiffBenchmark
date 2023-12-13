package rq;

/* Created by pourya on 2023-12-04 2:15 p.m. */

import benchmark.metrics.computers.vanilla.CommitPerfectRatioBenchmarkComputer;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.IOException;
import java.util.Map;

/***
 * What's the perfection ratio for each tool
 */
public class RQ8{

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
        try {
            new RQ8().run(new Configuration[]{ConfigurationFactory.refOracleTwoPointOne()});
            new RQ8().run(new Configuration[]{ConfigurationFactory.refOracle()});
            new RQ8().run(new Configuration[]{ConfigurationFactory.defects4jTwoPointOne()});
            new RQ8().run(new Configuration[]{ConfigurationFactory.defects4j()});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
