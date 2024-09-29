package benchmark.generators;


import benchmark.data.exp.EExperiment;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class MakeToolsOutput {

    public static void main(String[] args) throws Exception {
        new BenchmarkHumanReadableDiffGenerator(EExperiment.REF_EXP_3_0).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(EExperiment.REF_EXP_2_1).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(EExperiment.D4J_EXP_3_0).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(EExperiment.D4J_EXP_2_1).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(EExperiment.DUM_EXP_3_0).generateMultiThreaded();
        new BenchmarkHumanReadableDiffGenerator(EExperiment.D4J_EXP_2_1).generateMultiThreaded();
    }
}
