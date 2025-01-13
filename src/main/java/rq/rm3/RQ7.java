package rq.rm3;

/* Created by pourya on 2023-12-04 2:15 p.m. */

import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.metrics.computers.vanilla.CommitPerfectRatioBenchmarkComputer;
import rq.RQ;

import java.io.IOException;
import java.util.Map;

/***
 * What's the perfection ratio for each tool
 */
public class RQ7 implements RQ {
    @Override
    public void run(IExperiment[] experiments) {
        try {
            rq7(experiments);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void rq7(IExperiment[] confs) throws IOException {
        for (IExperiment experiment : confs) {
            Map<IASTDiffTool, Integer> astDiffToolIntegerMap = new CommitPerfectRatioBenchmarkComputer(experiment).perfectRatio();
            RQ.writeToFile(astDiffToolIntegerMap, "out/rq7-" + experiment.getName() + ".csv");
        }
    }
}
