package benchmark.generators;


import benchmark.data.exp.ExperimentsEnum;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {

    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(
                ExperimentsEnum.INCOMPATIBLE_TOOLS_TRIAL
        ).generateMultiThreaded();
    }
}
