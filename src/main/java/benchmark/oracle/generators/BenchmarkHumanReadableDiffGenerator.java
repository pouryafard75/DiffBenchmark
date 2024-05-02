package benchmark.oracle.generators;


import benchmark.oracle.generators.diff.HumanReadableDiffGenerator;
import benchmark.oracle.generators.tools.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.GenerationStrategy;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {

    private final Configuration configuration;

    public BenchmarkHumanReadableDiffGenerator(Configuration current){
        this.configuration = current;
    }
    public void generateMultiThreaded(int numThreads) {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(configuration.getAllCases().size());
        for (CaseInfo info : configuration.getAllCases()) {
            executorService.submit(() -> {
                try {
                    writeActiveTools(info, configuration.getOutputFolder());
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
        for (CaseInfo info : configuration.getAllCases()) {
            writeActiveTools(info, configuration.getOutputFolder());
        }
        System.out.println("Finished generating human readable diffs...");
    }
    private void writeActiveTools(CaseInfo info, String output_folder) throws Exception {
        String repo = info.getRepo();
        String commit = info.getCommit();
        System.out.println("Started for " + repo + " " + commit);
        ProjectASTDiff projectASTDiff = runWhatever(repo, commit);
        Set<ASTDiff> astDiffs = projectASTDiff.getDiffSet();
        for (ASTDiff astDiff : astDiffs) {
            //----------------------------------\\
            for (ASTDiffTool tool : configuration.getActiveTools()) {
                String toolName = tool.name(); //In case we later introduce a map from tool's name to tool's path
                String toolPath = tool.name(); //In case we later introduce a map from tool's name to tool's path
                ASTDiff generated = tool.diff(projectASTDiff, astDiff, info, configuration);
                GenerationStrategy generationStrategy = configuration.getGenerationStrategy();
                HumanReadableDiffGenerator humanReadableDiffGenerator = generationStrategy.getGenerator(projectASTDiff, generated, info, configuration);
                humanReadableDiffGenerator.write(output_folder,astDiff.getSrcPath(),toolPath);
            }
        }
        System.out.println("Finished for " + repo + " " + commit);
    }
}
