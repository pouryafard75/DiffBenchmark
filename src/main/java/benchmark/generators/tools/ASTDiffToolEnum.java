package benchmark.generators.tools;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.ASTDiffProviderForBenchmark;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.generators.tools.runners.IASTMapper;
import benchmark.generators.tools.runners.converter.PerfectDiff;
import benchmark.generators.tools.runners.converter.Spoon;
import benchmark.generators.tools.runners.gt.GreedyGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.SimpleGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.experimental.all.ModifierInterConservativeMulti;
import benchmark.generators.tools.runners.experimental.all.ModifierInterNoMulti;
import benchmark.generators.tools.runners.experimental.interfile.ProjectGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.experimental.interfile.ProjectGumTreeOptimizer;
import benchmark.generators.tools.runners.experimental.interfile.SingleVirtualNodeMatching;
import benchmark.generators.tools.runners.experimental.interfile.StagedTreeMatching;
import benchmark.generators.tools.runners.experimental.labels.GumTreeWithTreeModifier;
import benchmark.generators.tools.runners.experimental.labels.LeafLabelMerger;
import benchmark.generators.tools.runners.experimental.labels.LeafTypeMerger;
import benchmark.generators.tools.runners.experimental.multimapping.CopyPaste;
import benchmark.generators.tools.runners.experimental.multimapping.GumTreeWithMultiMappingASTDiffProvider;
import benchmark.generators.tools.runners.experimental.multimapping.NonMatchedSubtreesAdditionalRound;
import benchmark.generators.tools.runners.shaded.IJM;
import benchmark.generators.tools.runners.shaded.MTDiff;
import benchmark.generators.tools.runners.trivial.TrivialDiff;
import benchmark.utils.Experiments.IQuerySelector;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.models.ASTDiff;


public enum ASTDiffToolEnum implements IASTDiffTool {
    GOD ("PerfectDiff", PerfectDiff::new)
    ,
    RMD ("RefactoringMiner", (benchmarkCase, query) -> () -> query.apply(benchmarkCase.getProjectASTDiff()))
    ,
    GTG ("GumTree Greedy 3.0", (benchmarkCase, query) -> new GreedyGumTreeASTDiffProvider(query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    GTS ("GumTree Simple 3.0", (benchmarkCase, query) -> new SimpleGumTreeASTDiffProvider(query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    IJM ("Iterative Java Matcher", IJM::new)
    ,
    MTD ("MoveOptimized Tree Differencing", MTDiff::new)
    ,
    GT2 ("GumTree 2.0", benchmark.generators.tools.runners.shaded.GT2::new)
    ,
    IAM ("IASTMapper", IASTMapper::new)
    ,
    DAT ("Diff Auto Tuning", null)
    ,
    RM2 ("RefactoringMiner 2.0", benchmark.generators.tools.runners.converter.RM2::new)
    ,
    TRV ("TrivialDiff", TrivialDiff::new)
    ,
    VNG ("VirtualNodeWithGreedy", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        benchmarkCase, query,
                        new CompositeMatchers.ClassicGumtree())))
    ,
    VNS ("VirtualNodeWithSimple", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        benchmarkCase, query,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    SMG ("StagedTreeMatchingGreedy", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                        benchmarkCase, query,
                        new CompositeMatchers.ClassicGumtree())))

    ,
    SMS ("StagedTreeMatchingSimple", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                        benchmarkCase, query,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    NMG ("NonMappedSubTreesWithGreedy", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.ClassicGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    NMS ("NonMappedSubTreesWithSimple", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.SimpleGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    CPG("CopyPasteWithGreedy", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.ClassicGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    CPS ("CopyPasteWithSimple", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.SimpleGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    FLG ("FineGrainedLabelsWithGreedy", (benchmarkCase, query) ->
            new GumTreeWithTreeModifier(
                    new LeafLabelMerger(),
                    new CompositeMatchers.ClassicGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    FLS ("FineGrainedLabelsWithSimple", (benchmarkCase, query) ->
            new GumTreeWithTreeModifier(
                    new LeafLabelMerger(),
                    new CompositeMatchers.SimpleGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    FTG ("FineGrainedTypesWithGreedy", (benchmarkCase, query) ->
            new GumTreeWithTreeModifier(
                    new LeafTypeMerger(),
                    new CompositeMatchers.ClassicGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    FTS ("FineGrainedTypesWithSimple",(benchmarkCase, query) ->
            new GumTreeWithTreeModifier(
                    new LeafTypeMerger(),
                    new CompositeMatchers.SimpleGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    COMBINED_TYPE_STAGED_GREEDY ("COMBINED_TYPE_STAGED_GREEDY", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.ClassicGumtree())))),
    COMBINED_TYPE_STAGED_SIMPLE ("COMBINED_TYPE_STAGED_SIMPLE", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    COMBINED_TYPE_VN_GREEDY ("COMBINED_TYPE_SVN_GREEDY", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new SingleVirtualNodeMatching(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.ClassicGumtree())))),
    COMBINED_TYPE_VN_SIMPLE ("COMBINED_TYPE_SVN_SIMPLE",  ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new SingleVirtualNodeMatching(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    X_TYPE_STAGED_NONMATCHED_GREEDY ("MERGED_STAGED_NONMATCHED_GREEDY",  ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                            new NonMatchedSubtreesAdditionalRound(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.ClassicGumtree()))))
    ,
    X_TYPE_STAGED_NONMATCHED_SIMPLE ("MERGED_STAGED_NONMATCHED_SIMPLE", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                            new NonMatchedSubtreesAdditionalRound(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    SPN ("Spoon", Spoon::new)

    ;

    private final ASTDiffProviderForBenchmark factory;
    private final String toolName;

    ASTDiffToolEnum(String toolName, ASTDiffProviderForBenchmark factory) {
        this.factory = factory;
        this.toolName = toolName;
    }

    public String getToolName() {
        return toolName;
    }
    public String getNameInURL() {
        return this.name();
    }

    @Override
    public ASTDiffProvider get(IBenchmarkCase benchmarkCase, IQuerySelector query) {
        return factory.get(benchmarkCase, query);
    }

    public ASTDiff diff(IBenchmarkCase benchmarkCase, IQuerySelector querySelector) throws Exception {
        return factory.get(benchmarkCase, querySelector).getASTDiff();
    }
}



