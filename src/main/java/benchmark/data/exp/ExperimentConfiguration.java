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

    private String outputFolder = "output/";
    private String csvDestinationFile = "stats.csv";
    private String name = "no-name";


    public ExperimentConfiguration(IBenchmarkDataset iBenchmarkDataset, GenerationStrategy generationStrategy, Set<IASTDiffTool> tools) {
        this.dataset = iBenchmarkDataset;
        this.generationStrategy = generationStrategy;
        this.tools = tools;
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

    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
        if (!outputFolder.endsWith("/"))
            this.outputFolder += "/";
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setCsvDestinationFile(String csvDestinationFile) {
        this.csvDestinationFile = csvDestinationFile;
    }


    public void setTools(Set<IASTDiffTool> tools) {
        this.tools = tools;
    }
}



