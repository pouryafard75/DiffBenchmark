package benchmark.data.exp;

import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.data.diffcase.BenchmarkCase;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.utils.Experiments.GenerationStrategy;

import java.nio.file.Path;
import java.util.*;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class ExperimentConfiguration implements IExperiment {
    private Set<IASTDiffTool> activeTools;
    private final IBenchmarkDataset dataset;
    private final GenerationStrategy generationStrategy;

    private String outputFolder = "output/";
    private String csvDestinationFile = "stats.csv";
    private String name = "no-name";

    @Override
    public String getOutputFolder() {
        return outputFolder;
    }

    @Override
    public IBenchmarkDataset getDataset() {
        return dataset;
    }

    @Override
    public GenerationStrategy getGenerationStrategy() {
        return generationStrategy;
    }

    @Override
    public Set<IASTDiffTool> getTools() {
        return Set.of();
    }

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
        if (!outputFolder.endsWith("/"))
            this.outputFolder += "/";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setCsvDestinationFile(String csvDestinationFile) {
        this.csvDestinationFile = csvDestinationFile;
    }


    public ExperimentConfiguration(IBenchmarkDataset iBenchmarkDataset, GenerationStrategy generationStrategy, Set<IASTDiffTool> activeTools) {
        this.dataset = iBenchmarkDataset;
        this.generationStrategy = generationStrategy;
        this.activeTools = activeTools;
    }
//    private static Set<ASTDiffTool> populateTools(Compatibility compatibility) {
//        Set<ASTDiffTool> tools = new LinkedHashSet<>();
//        tools.add(ASTDiffTool.GOD);
//        tools.add(ASTDiffTool.TRV);
//        tools.addAll(Arrays.asList(compatibility.getTools()));
//        return tools;
//    }
    public ASTDiffTool[] getActiveTools() {
        ASTDiffTool[] array = activeTools.toArray(new ASTDiffTool[0]);
        return array;
    }

    public void setActiveTools(Set<IASTDiffTool> activeTools) {
        this.activeTools = activeTools;
    }

    public Path getPerfectDiffDir() {
        return dataset.getPerfectDirPath();
    }

    public Set<? extends BenchmarkCase> getAllCases() {
        return dataset.getCases();
    }


    public String getCsvDestinationFile() {
        return csvDestinationFile;
    }

    public static String getMergedNames(IExperiment[] experiments) {
        StringBuilder configNames = new StringBuilder();
        for (IExperiment experiment : experiments) {
            configNames.append(experiment.getName()).append("-");
        }
        return configNames.toString();
    }
}



