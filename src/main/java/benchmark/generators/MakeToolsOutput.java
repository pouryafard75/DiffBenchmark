package benchmark.generators;


import benchmark.data.exp.ExperimentsEnum;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {

    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(ExperimentsEnum.REF_EXP_3_0_COMMENTS).generateMultiThreaded();
//        new BenchmarkHumanReadableDiffGenerator(ExperimentsEnum.REF_EXP_3_0).generateMultiThreaded();
//        new BenchmarkHumanReadableDiffGenerator(ExperimentsEnum.REF_EXP_2_1).generateMultiThreaded();
//        new BenchmarkHumanReadableDiffGenerator(ExperimentsEnum.D4J_EXP_3_0).generateMultiThreaded();
//        new BenchmarkHumanReadableDiffGenerator(ExperimentsEnum.D4J_EXP_2_1).generateMultiThreaded();
//        new BenchmarkHumanReadableDiffGenerator(ExperimentsEnum.DUM_EXP_3_0).generateMultiThreaded();
//        new BenchmarkHumanReadableDiffGenerator(ExperimentsEnum.D4J_EXP_2_1).generateMultiThreaded();
    }
}
