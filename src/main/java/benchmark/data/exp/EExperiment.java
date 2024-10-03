package benchmark.data.exp;

import benchmark.data.dataset.EBenchmarkDataset;
import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.utils.Experiments.GenerationStrategy;
import benchmark.utils.Experiments.IGenerationStrategy;

import java.util.Set;

public enum EExperiment implements IExperiment {
    REF_EXP_3_0(EBenchmarkDataset.RefOracle, ToolSets.THREE_ZERO_COMPATIBLE, GenerationStrategy.NonFiltered, "output/RefOracle3_0/", "RefOracle3_0.csv"),
    REF_EXP_2_1(EBenchmarkDataset.RefOracle, ToolSets.TWO_ONE_COMPATIBLE,GenerationStrategy.Filtered, "output/RefOracle2_1/", "RefOracle3_0.csv"),
    D4J_EXP_3_0(EBenchmarkDataset.Defects4J, ToolSets.THREE_ZERO_COMPATIBLE,GenerationStrategy.NonFiltered, "output/D4JOracle3_0/", "D4JOracle3_0.csv"),
    D4J_EXP_2_1(EBenchmarkDataset.Defects4J, ToolSets.TWO_ONE_COMPATIBLE,GenerationStrategy.Filtered, "output/D4JOracle2_1/", "D4JOracle2_1.csv"),
    DUM_EXP_3_0(EBenchmarkDataset.Dummy, ToolSets.THREE_ZERO_COMPATIBLE,GenerationStrategy.NonFiltered, "output/DumOracle3_0/", "DumOracle3_0.csv"),
    DUM_EXP_2_1(EBenchmarkDataset.Dummy, ToolSets.TWO_ONE_COMPATIBLE,GenerationStrategy.NonFiltered, "output/DumOracle2_1/", "DumOracle2_1.csv"),
    ;

    private final IBenchmarkDataset benchmarkDatasets;
    private final GenerationStrategy generationStrategy;
    private final String outputFolder;
    private final String csvDestinationFile;
    private Set<IASTDiffTool> tools;

    EExperiment(IBenchmarkDataset benchmarkDatasets, Set<IASTDiffTool> tools, GenerationStrategy generationStrategy, String outputFolder, String csvDestinationFile) {
        this.benchmarkDatasets = benchmarkDatasets;
        this.tools = tools;
        this.generationStrategy = generationStrategy;
        this.outputFolder = outputFolder;
        this.csvDestinationFile = csvDestinationFile;
    }

    public void setTools(Set<IASTDiffTool> tools) {
        this.tools = tools;
    }

    @Override
    public IBenchmarkDataset getDataset() {
        return benchmarkDatasets;
    }

    @Override
    public Set<IASTDiffTool> getTools() {
        return this.tools;
    }

    @Override
    public IGenerationStrategy getGenerationStrategy() {
        return generationStrategy;
    }

    @Override
    public String getOutputFolder() {
        return outputFolder;
    }

    @Override
    public String getCsvDestinationFile() {
        return csvDestinationFile;
    }

    @Override
    public String getName() {
        return "";
    }


    public static IExperiment findExperimentByName(String arg) {
        switch (arg) {
            case "refOracle":
                return EExperiment.REF_EXP_3_0;
            case "refOracleTwoPointOne":
                return EExperiment.REF_EXP_2_1;
            case "defects4j":
                return EExperiment.D4J_EXP_3_0;
            case "defects4jTwoPointOne":
                return EExperiment.D4J_EXP_2_1;
            case "dummy":
                return EExperiment.DUM_EXP_3_0;
            case "dummyTwoPointOne":
                return EExperiment.DUM_EXP_2_1;
        }
        return null;
    }

}
