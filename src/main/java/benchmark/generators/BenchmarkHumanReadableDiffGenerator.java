package benchmark.generators;


import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.hrd.HumanReadableDiffGenerator;
import benchmark.generators.tools.models.IASTDiffTool;
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
        Set<? extends BenchmarkCase> cases = experiment.getDataset().getCases();
        CountDownLatch latch = new CountDownLatch(cases.size());
        for (BenchmarkCase info : cases) {
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
        for (BenchmarkCase info : experiment.getDataset().getCases()) {
            writeActiveTools(info, experiment.getOutputFolder());
        }
        System.out.println("Finished generating human readable diffs...");
    }
    private void writeActiveTools(BenchmarkCase info, String output_folder) throws Exception {
        String repo = info.getRepo();
        String commit = info.getCommit();
        System.out.println("Started for " + repo + " " + commit);
        ProjectASTDiff projectASTDiff = runWhatever(repo, commit);
        Set<ASTDiff> astDiffs = projectASTDiff.getDiffSet();
        for (ASTDiff astDiff : astDiffs) {
            //----------------------------------\\
            for (IASTDiffTool tool : experiment.getTools()) {
                String toolName = tool.getToolName(); //In case we later introduce a map from tool's name to tool's path
                String toolPath = tool.getToolName(); //In case we later introduce a map from tool's name to tool's path
                ASTDiff generated = tool.getASTDiffer(projectASTDiff, astDiff, info).makeASTDiff();
                IGenerationStrategy generationStrategy = experiment.getGenerationStrategy();
                HumanReadableDiffGenerator humanReadableDiffGenerator = generationStrategy.getGenerator(projectASTDiff, generated, info);
                humanReadableDiffGenerator.write(output_folder,astDiff.getSrcPath(),toolPath);
            }
        }
        System.out.println("Finished for " + repo + " " + commit);
    }
}
