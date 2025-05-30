package benchmark.data.exp;

import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.ASTDiffProviderForBenchmark;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.models.IGenerationStrategy;

import java.util.Set;

public interface IExperiment {
    /**
     * Get the dataset of the experiment
     */
    IBenchmarkDataset getDataset();
    /**
     * Get the tools that are active in the experiment
     */
    Set<IASTDiffTool> getTools();
    /**
     * Get the generation strategy of the experiment
     */
    IGenerationStrategy getGenerationStrategy();
    /**
     * Get the name of the experiment
     */
    String getName();
    String getOutputFolder();
    String getCsvDestinationFile();

    default ASTDiffProviderForBenchmark getTRVProvider(){
        return ASTDiffToolEnum.TRV;
    }
    default ASTDiffProviderForBenchmark getGODProvider(){
        return ASTDiffToolEnum.GOD;
    }


    static String getMergedNames(IExperiment[] experiments) {
        StringBuilder configNames = new StringBuilder();
        for (IExperiment experiment : experiments) {
            configNames.append(experiment.getName()).append("-");
        }
        return configNames.toString();
    }

    void setTools(Set<IASTDiffTool> tools);
}
