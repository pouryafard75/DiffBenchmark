package benchmark.generators;


import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.ASTDiffToolEnum;

import java.util.Set;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {

    public static void main(String[] args) throws Exception {
        IExperiment experiment = ExperimentsEnum.VISITOR_EXP;
        experiment.setTools(Set.of(ASTDiffToolEnum.GTZ___));
        new BenchmarkHumanReadableDiffGenerator(
                experiment
        ).generateSingleThreaded();
    }
}
