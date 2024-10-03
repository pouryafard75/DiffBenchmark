package benchmark.generators;


import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.hrd.HumanReadableDiffGenerator;
import benchmark.generators.tools.models.IASTDiffTool;

import benchmark.models.HumanReadableDiff;
import benchmark.utils.Experiments.IGenerationStrategy;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {

    private final IExperiment experiment;

    public BenchmarkHumanReadableDiffGenerator(IExperiment experiment){
        this.experiment = experiment;
    }
    public void generateMultiThreaded(int numThreads) {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        Set<? extends IBenchmarkCase> cases = experiment.getDataset().getCases();
        CountDownLatch latch = new CountDownLatch(cases.size());
        for (IBenchmarkCase info : cases) {
            executorService.submit(() -> {
                try {
                    writeActiveTools(info, experiment.getOutputFolder());
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.exit(1); // Terminate execution with status code 1
                    throw new RuntimeException(e);

                } finally {
                    latch.countDown();
                }
            });
        }

        executorService.shutdown();
        try {
            // Wait until all threads finish their work
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Finished generating human readable diffs...");
    }
    public void generateMultiThreaded(){
        generateMultiThreaded(Runtime.getRuntime().availableProcessors());
    }

    public void generateSingleThreaded() throws Exception {
        for (IBenchmarkCase info : experiment.getDataset().getCases()) {
            writeActiveTools(info, experiment.getOutputFolder());
        }
        System.out.println("Finished generating human readable diffs...");
    }
    private void writeActiveTools(IBenchmarkCase benchmarkCase, String output_folder) throws Exception {
        String repo = benchmarkCase.getRepo();
        String commit = benchmarkCase.getCommit();
        System.out.println("Started for " + repo + " " + commit);
        ProjectASTDiff projectASTDiff = runWhatever(repo, commit);
        Set<ASTDiff> astDiffs = projectASTDiff.getDiffSet();
        for (ASTDiff astDiff : astDiffs) {
            for (IASTDiffTool tool : experiment.getTools()) {
                String toolPath = tool.getToolName();
                ASTDiff generated = tool.get(benchmarkCase, (x -> astDiff)).getASTDiff();
                IGenerationStrategy generationStrategy = experiment.getGenerationStrategy();
                HumanReadableDiff hrd = generationStrategy.get(benchmarkCase, (x -> generated));
                hrd.write(output_folder,astDiff.getSrcPath(),toolPath,commit, repo); //TODO : verify
            }
        }
        System.out.println("Finished for " + repo + " " + commit);
    }
}
