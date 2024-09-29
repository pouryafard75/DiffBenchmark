package benchmark.data.exp;

import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.utils.Experiments.IGenerationStrategy;

import java.util.Set;

public interface IExperiment {
    IBenchmarkDataset getDataset();
    Set<IASTDiffTool> getTools();
    IGenerationStrategy getGenerationStrategy();
    String getOutputFolder();
    String getCsvDestinationFile();
    String getName();


    static String getMergedNames(IExperiment[] experiments) {
        StringBuilder configNames = new StringBuilder();
        for (IExperiment experiment : experiments) {
            configNames.append(experiment.getName()).append("-");
        }
        return configNames.toString();
    }
}
