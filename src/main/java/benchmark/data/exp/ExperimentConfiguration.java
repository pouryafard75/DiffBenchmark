package benchmark.data.exp;

import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.utils.Experiments.GenerationStrategy;

import java.util.*;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class ExperimentConfiguration implements IExperiment {
    private Set<IASTDiffTool> tools;
    private final IBenchmarkDataset dataset;
    private final GenerationStrategy generationStrategy;

    private final String outputFolder;
    private final String csvDestinationFile;
    private final String name;


    public ExperimentConfiguration(IBenchmarkDataset iBenchmarkDataset, Set<IASTDiffTool> tools, GenerationStrategy generationStrategy,
                                   String outputFolder, String csvDestinationFile, String name) {
        this.dataset = iBenchmarkDataset;
        this.generationStrategy = generationStrategy;
        this.tools = tools;
        this.outputFolder = outputFolder;
        this.csvDestinationFile = csvDestinationFile;
        this.name = name;
    }

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
        return tools;
    }
    public String getName() {
        return name;
    }

    public String getCsvDestinationFile() {
        return csvDestinationFile;
    }

    @Override
    public void setTools(Set<IASTDiffTool> tools) {
        this.tools = tools;
    }
}



