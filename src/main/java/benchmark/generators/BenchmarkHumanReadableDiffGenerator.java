package benchmark.generators;


import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.ASTDiffProviderForBenchmark;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.models.hrd.HumanReadableDiff;
import benchmark.models.IGenerationStrategy;
import benchmark.models.selector.DiffSelector;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {

    private final IExperiment experiment;

    
    private String TRV_NAME = "TRV";
    private String GOD_NAME = "GOD";


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
                    e.printStackTrace();
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
        generateMultiThreaded(Runtime.getRuntime().availableProcessors() / 2);
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
        ProjectASTDiff projectASTDiff = benchmarkCase.getProjectASTDiff();
        Set<ASTDiff> astDiffs = projectASTDiff.getDiffSet();
        Set<IASTDiffTool> tools = new LinkedHashSet<>(experiment.getTools());
        addCustomTool(tools, TRV_NAME, experiment.getTRVProvider());
        addCustomTool(tools, GOD_NAME, experiment.getGODProvider());
        for (ASTDiff astDiff : astDiffs) {
            for (IASTDiffTool tool : tools) {
                ASTDiff generated = tool.apply(benchmarkCase, (p -> astDiff)).getASTDiff();
                IGenerationStrategy generationStrategy = experiment.getGenerationStrategy();
                HumanReadableDiff hrd = generationStrategy.apply(benchmarkCase,generated);
                String shortName = tool.getShortName();
                hrd.write(output_folder,astDiff.getSrcPath(), shortName,commit, repo); //TODO : verify
            }
        }
        System.out.println("Finished for " + repo + " " + commit);
    }

    private void addCustomTool(Set<IASTDiffTool> tools, final String toolName, final ASTDiffProviderForBenchmark provider) {
        IASTDiffTool custom = new IASTDiffTool() {
            @Override
            public String getToolName() {
                return toolName;
            }

            @Override
            public String getShortName() {
                return toolName;
            }

            @Override
            public ASTDiffProvider apply(IBenchmarkCase benchmarkCase, DiffSelector query) {
                return provider.apply(benchmarkCase, query);
            }
        };
        tools.add(custom);
    }
}
