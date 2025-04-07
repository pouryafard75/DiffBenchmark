package benchmark.data.exp;

import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.ASTDiffProviderForBenchmark;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.models.IGenerationStrategy;

import java.util.Set;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class ExperimentImpl implements IExperiment {
    private Set<IASTDiffTool> tools;
    private final IBenchmarkDataset dataset;
    private final IGenerationStrategy generationStrategy;
    private final String outputFolder;
    private final String csvDestinationFile;
    private final String name;

    private ASTDiffProviderForBenchmark customTRV;
    private ASTDiffProviderForBenchmark customGOD;

    @Override
    public ASTDiffProviderForBenchmark getGODProvider() {
        return (customGOD == null) ? null : customGOD;
    }

    @Override
    public ASTDiffProviderForBenchmark getTRVProvider() {
        return (customTRV == null) ? null : customTRV;
    }


    public ExperimentImpl(IBenchmarkDataset iBenchmarkDataset, Set<IASTDiffTool> tools, IGenerationStrategy generationStrategy,
                          String hrdOraclePath, String csvStatsPath, String name) {
        this(iBenchmarkDataset, tools, generationStrategy, hrdOraclePath, csvStatsPath, name,
                ASTDiffToolEnum.TRV, ASTDiffToolEnum.GOD);
    }

    public ExperimentImpl(IBenchmarkDataset iBenchmarkDataset, Set<IASTDiffTool> tools, IGenerationStrategy generationStrategy,
                          String outputFolder, String csvDestinationFile, String name, ASTDiffProviderForBenchmark customTRV, ASTDiffProviderForBenchmark customGOD) {
        this.dataset = iBenchmarkDataset;
        this.generationStrategy = generationStrategy;
        this.tools = tools;
        this.outputFolder = outputFolder;
        this.csvDestinationFile = csvDestinationFile;
        this.name = name;
        this.customTRV = customTRV;
        this.customGOD = customGOD;
    }

    public ExperimentImpl(IExperiment experiment) {
        this.dataset = experiment.getDataset();
        this.generationStrategy = experiment.getGenerationStrategy();
        this.tools = experiment.getTools();
        this.outputFolder = experiment.getOutputFolder();
        this.csvDestinationFile = experiment.getCsvDestinationFile();
        this.name = experiment.getName();
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
    public IGenerationStrategy getGenerationStrategy() {
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



