package benchmark.data.exp;

import benchmark.data.dataset.EBenchmarkDataset;
import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.generators.hrd.GenerationStrategy;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.ASTDiffProviderForBenchmark;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.models.IGenerationStrategy;

import java.util.Set;

public enum ExperimentsEnum implements IExperiment {
    // RM 3.0 Experiments
    REF_EXP_3_0(new ExperimentImpl(EBenchmarkDataset.RefOracle,
            ToolSets.THREE_ZERO_COMPATIBLE,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "RefOracle3_0/",
            "RefOracle3_0.csv",
            "ref3.0")),
    REF_EXP_2_1(new ExperimentImpl(EBenchmarkDataset.RefOracle,
            ToolSets.TWO_ONE_COMPATIBLE,
            GenerationStrategy.Filtered,
            "RefOracle2_1/",
            "RefOracle3_0.csv",
            "ref2.1")),
    D4J_EXP_3_0(new ExperimentImpl(EBenchmarkDataset.Defects4J,
            ToolSets.THREE_ZERO_COMPATIBLE,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "D4JOracle3_0/",
            "D4JOracle3_0.csv",
            "d4j3.0")),
    D4J_EXP_2_1(new ExperimentImpl(EBenchmarkDataset.Defects4J,
            ToolSets.TWO_ONE_COMPATIBLE,
            GenerationStrategy.Filtered,
            "D4JOracle2_1/",
            "D4JOracle2_1.csv",
            "d4j2.1")),
    DUM_EXP_3_0(new ExperimentImpl(EBenchmarkDataset.Dummy,
            ToolSets.THREE_ZERO_COMPATIBLE,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "DumOracle3_0/",
            "DumOracle3_0.csv",
            "dum3.0")),
    DUM_EXP_2_1(new ExperimentImpl(EBenchmarkDataset.Dummy,
            ToolSets.TWO_ONE_COMPATIBLE,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "DumOracle2_1/",
            "DumOracle2_1.csv",
            "dum2.1")),
    REF_COMMENTS_3_0(new ExperimentImpl(EBenchmarkDataset.RefOracle,
            ToolSets.THREE_ZERO_COMPATIBLE,
            GenerationStrategy.NonFiltered_CommentsOnly,
            "comments_ref/",
            "out.csv",
            "ref3.0_comments",
            ASTDiffToolEnum.EMP,
            ASTDiffToolEnum.RMD))
    ,
    D4J_COMMENTS_3_0(new ExperimentImpl(EBenchmarkDataset.Defects4J,
            ToolSets.THREE_ZERO_COMPATIBLE,
            GenerationStrategy.NonFiltered_CommentsOnly,
            "comments_d4j/",
            "out.csv",
            "d4j3.0_comments",
            ASTDiffToolEnum.EMP,
            ASTDiffToolEnum.RMD))
    ,

    //DiffBenchmark paper experiments
    INTER_FILE_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.INTERFILE_EXTENSION_BATTLE_TOOLS,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/literature-exp/",
            "out.csv",
            "interfile-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,
    MULTI_MAPPING_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.MULTIMAPPING_EXTENSION_BATTLE_TOOLS,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/literature-exp/",
            "out.csv",
            "multimapping-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

    MULTI_MAPPING_SEM_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.MULTI_MAPPING_SEM_BATTLE_TOOLS,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/mm-sem-exp/",
            "out.csv",
            "mm-sem-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

    SEMANTIC_VIOLATION_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.SEMANTIC_VIOLATION_EXTENSION_BATTLE_TOOLS,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/literature-exp/",
            "out.csv",
            "semantic-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

    ARTIFICIAL_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.ARTIFICIAL,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/artificial/",
            "out.csv",
            "skirmish",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

    VISITOR_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.VISITOR_EXP_BATTLE_TOOLS,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/literature-exp/",
            "out.csv",
            "visitor-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,
    VISITOR_EXP_SINGLE_CASE(new ExperimentImpl(
            EBenchmarkDataset.RefOracleSingleCase,
            ToolSets.VISITOR_EXP_BATTLE_TOOLS,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/visitors-exp-single-case/",
            "out.csv",
            "visitor-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

    LITERATURE_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.ALL,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/literature-exp/",
            "out.csv",
            "literature-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,
    D4J_EXP(new ExperimentImpl(
            EBenchmarkDataset.Defects4J,
            Set.of(ASTDiffToolEnum.RMD),
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/d4j-exp/",
            "out.csv",
            "d4j-literature-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

    SINGLE_CASE(new ExperimentImpl(
            EBenchmarkDataset.RefOracleSingleCase,
                   ToolSets.ALL,
                   GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/literature-exp/",
                           "out.csv",
                           "literature-exp",
                   ASTDiffToolEnum.TRV,
                   ASTDiffToolEnum.GOD))
    ,

    SPOON_EXP(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            Set.of(ASTDiffToolEnum.SPN_T),
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/SPOON-exp/",
            "out.csv",
            "spoon-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

    ADB_PAPER_FULL(new ExperimentImpl(
            EBenchmarkDataset.RefOracle,
            ToolSets.ADB_PAPER_FULL,
            GenerationStrategy.NO_COMMENTS_AND_JAVADOCS,
            "hrd-oracle/adb-paper/SPOON-exp/",
            "out.csv",
            "spoon-exp",
            ASTDiffToolEnum.TRV,
            ASTDiffToolEnum.GOD))
    ,

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

    @Override
    public ASTDiffProviderForBenchmark getGODProvider() {
        return experiment.getGODProvider();
    }

    @Override
    public ASTDiffProviderForBenchmark getTRVProvider() {
        return experiment.getTRVProvider();
    }
}
