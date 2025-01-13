package benchmark.generators;


import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {

    public static void main(String[] args) throws Exception {
        IExperiment experiment = ExperimentsEnum.VISITOR_EXP;
        new BenchmarkHumanReadableDiffGenerator(
                experiment
        ).generateMultiThreaded();
    }
}
