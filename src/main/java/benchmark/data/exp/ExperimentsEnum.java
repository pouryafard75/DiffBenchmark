package benchmark.data.exp;

import benchmark.data.dataset.EBenchmarkDataset;
import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.utils.Experiments.GenerationStrategy;
import benchmark.utils.Experiments.IGenerationStrategy;

import java.util.Set;

public enum ExperimentsEnum implements IExperiment {
    REF_EXP_3_0(new ExperimentConfiguration(EBenchmarkDataset.RefOracle, ToolSets.THREE_ZERO_COMPATIBLE, GenerationStrategy.NonFiltered, "output/RefOracle3_0/", "RefOracle3_0.csv", "ref3.0")),
    REF_EXP_2_1(new ExperimentConfiguration(EBenchmarkDataset.RefOracle, ToolSets.TWO_ONE_COMPATIBLE,GenerationStrategy.Filtered, "output/RefOracle2_1/", "RefOracle3_0.csv", "ref2.1")),
    D4J_EXP_3_0(new ExperimentConfiguration(EBenchmarkDataset.Defects4J, ToolSets.THREE_ZERO_COMPATIBLE,GenerationStrategy.NonFiltered, "output/D4JOracle3_0/", "D4JOracle3_0.csv", "d4j3.0")),
    D4J_EXP_2_1(new ExperimentConfiguration(EBenchmarkDataset.Defects4J, ToolSets.TWO_ONE_COMPATIBLE,GenerationStrategy.Filtered, "output/D4JOracle2_1/", "D4JOracle2_1.csv", "d4j2.1")),
    DUM_EXP_3_0(new ExperimentConfiguration(EBenchmarkDataset.Dummy, ToolSets.THREE_ZERO_COMPATIBLE,GenerationStrategy.NonFiltered, "output/DumOracle3_0/", "DumOracle3_0.csv", "dum3.0")),
    DUM_EXP_2_1(new ExperimentConfiguration(EBenchmarkDataset.Dummy, ToolSets.TWO_ONE_COMPATIBLE,GenerationStrategy.NonFiltered, "output/DumOracle2_1/", "DumOracle2_1.csv", "dum2.1")),

    REF_EXP_3_0_COMMENTS(new ExperimentConfiguration(EBenchmarkDataset.RefOracle, ToolSets.THREE_ZERO_COMPATIBLE, GenerationStrategy.Comments, "output/comments/", "RefOracle3_0.csv", "ref3.0_comments")),
    ;


    private final IExperiment experiment;

    ExperimentsEnum(IExperiment experiment) {
        this.experiment = experiment;
    }

    @Override
    public IBenchmarkDataset getDataset() {
        return experiment.getDataset();
    }

    @Override
    public Set<IASTDiffTool> getTools() {
        return experiment.getTools();
    }

    @Override
    public IGenerationStrategy getGenerationStrategy() {
        return experiment.getGenerationStrategy();
    }

    @Override
    public String getOutputFolder() {
        return experiment.getOutputFolder();
    }

    @Override
    public String getCsvDestinationFile() {
        return experiment.getCsvDestinationFile();
    }

    @Override
    public String getName() {
        return experiment.getName();
    }


    public static IExperiment findExperimentByName(String arg) {
        switch (arg) {
            case "refOracle":
                return ExperimentsEnum.REF_EXP_3_0;
            case "refOracleTwoPointOne":
                return ExperimentsEnum.REF_EXP_2_1;
            case "defects4j":
                return ExperimentsEnum.D4J_EXP_3_0;
            case "defects4jTwoPointOne":
                return ExperimentsEnum.D4J_EXP_2_1;
            case "dummy":
                return ExperimentsEnum.DUM_EXP_3_0;
            case "dummyTwoPointOne":
                return ExperimentsEnum.DUM_EXP_2_1;
        }
        return null;
    }

    public void setTools(Set<IASTDiffTool> tools) {
        experiment.setTools(tools);
    }
}