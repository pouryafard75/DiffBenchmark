package benchmark.generators.tools;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.ASTDiffProviderForBenchmark;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.generators.tools.runners.IASTMapper;
import benchmark.generators.tools.runners.converter.*;
import benchmark.generators.tools.runners.extensions.combined.ModifierInterConservativeMulti;
import benchmark.generators.tools.runners.extensions.interfile.ProjectGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.extensions.interfile.ProjectGumTreeOptimizer;
import benchmark.generators.tools.runners.extensions.interfile.SingleVirtualNodeMatching;
import benchmark.generators.tools.runners.extensions.interfile.StagedTreeMatching;
import benchmark.generators.tools.runners.extensions.labels.BlockAndSimpleNameModifier;
import benchmark.generators.tools.runners.extensions.labels.GumTreeWithTreeModifier;
import benchmark.generators.tools.runners.extensions.multimapping.CopyPaste;
import benchmark.generators.tools.runners.extensions.multimapping.GumTreeWithMultiMappingASTDiffProvider;
import benchmark.generators.tools.runners.extensions.multimapping.NonMatchedSubtreesAdditionalRound;
import benchmark.generators.tools.runners.gt.GreedyGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.OriginalVisitorGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.SimpleGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.shaded.GT2;
import benchmark.generators.tools.runners.shaded.IJM;
import benchmark.generators.tools.runners.shaded.MTDiff;
import benchmark.generators.tools.runners.trivial.EmptyDiff;
import benchmark.generators.tools.runners.trivial.TrivialDiff;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.models.ASTDiff;
import shadedspoon.com.github.gumtreediff.matchers.GumtreeProperties;
import shadedspoon.gumtree.spoon.diff.DiffConfiguration;


public enum ASTDiffToolEnum implements IASTDiffTool {
    /*
    On the naming conventions:
    - The suffix "_T" means that the tool is translated
    - The suffix "_G" means that the tool uses the greedy matcher
    - The suffix "_S" means that the tool uses the simple matcher
    - The suffix "_O" means that the tool uses the original visitor
    - The suffix "_I" means that the tool is incompatible with the benchmark
    - The suffix "___" means that the tools is experimental and should be only used for testing/visualization purposes
    Suffices can be combined to create a more descriptive name such as "SPN_G_T" which means that the tool is Spoon with the greedy matcher and translated
    Try your best to keep the names short and descriptive... but not too short that it becomes ambiguous,
    And Please don't use more than 3 letters for the name of the tool/algorithm, but decorate it with the suffixes to make it more descriptive
    Unless you are working on an extension, which must begin with _EXT and be followed by the name of each extension + the suffixes
    */
    GOD ("Ground Truth", PerfectDiff::new)
    ,
    RMD ("RefactoringMiner", (benchmarkCase, query) -> () -> query.apply(benchmarkCase.getProjectASTDiff()))
    ,
    GTG ("GumTree Greedy 3.0", (benchmarkCase, query) -> new GreedyGumTreeASTDiffProvider(query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    GTG_O("GumTree Greedy 3.0 Original Visitor", (benchmarkCase, query) ->
            new OriginalVisitorGumTreeASTDiffProvider(
                    new CompositeMatchers.ClassicGumtree(),
                    benchmarkCase,
                    query))
    ,
    GTS ("GumTree Simple 3.0", (benchmarkCase, query) -> new SimpleGumTreeASTDiffProvider(query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    GTS_O("GumTree Simple 3.0 Original Visitor", (benchmarkCase, query) ->
            new OriginalVisitorGumTreeASTDiffProvider(
                    new CompositeMatchers.SimpleGumtree(),
                    benchmarkCase,
                    query))
    ,
    IJM_I("Iterative Java Matcher", IJM::new)
    ,
    IJM_T("Iterative Java Matcher (Translated)",(benchmarkCase, query) -> new TwoPointOneTranslator(new IJM(benchmarkCase, query)))
    ,
    MTD_I("MoveOptimized Tree Differencing", MTDiff::new)
    ,
    MTD_T("MoveOptimized Tree Differencing (Translated)",(benchmarkCase, query) -> new TwoPointOneTranslator(new MTDiff(benchmarkCase, query)))
    ,
    GT2_I("GumTree 2.0", benchmark.generators.tools.runners.shaded.GT2::new)
    ,
    GT2_T("GumTree 2.0 (Translated)",(benchmarkCase, query) -> new TwoPointOneTranslator(new GT2(benchmarkCase, query)))
    ,
    IAM_I("IASTMapper", IASTMapper::new)
    ,
    IAM_T("IASTMapper (Translated)",(benchmarkCase, query) -> new TwoPointOneTranslator(new IASTMapper(benchmarkCase, query)))
    ,
    DAT ("Diff Auto Tuning", null)
    ,
    RM2 ("RefactoringMiner 2.0", benchmark.generators.tools.runners.converter.RM2::new)
    ,
    TRV ("TrivialDiff", TrivialDiff::new)
    ,
    EMP("Empty", (benchmarkCase, query) -> new EmptyDiff(query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    EXT_SVN_G("VirtualNodeWithGreedy", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        benchmarkCase, query,
                        new CompositeMatchers.ClassicGumtree())))
    ,
    EXT_SVN_S("VirtualNodeWithSimple", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        benchmarkCase, query,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    EXT_STM_G("StagedTreeMatchingGreedy", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                        benchmarkCase, query,
                        new CompositeMatchers.ClassicGumtree())))

    ,
    EXT_STM_S("StagedTreeMatchingSimple", (benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                        benchmarkCase, query,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    EXT_NMS_G("NonMappedSubTreesWithGreedy", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.ClassicGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    EXT_NMS_S("NonMappedSubTreesWithSimple", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.SimpleGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    EXT_COP_G("CopyPasteWithGreedy", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.ClassicGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    EXT_COP_S("CopyPasteWithSimple", (benchmarkCase, query) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.SimpleGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    EXT_FGT_G("FineGrainedTypesWithGreedy", (benchmarkCase, query) ->
            new GumTreeWithTreeModifier(
                    new BlockAndSimpleNameModifier(),
                    new CompositeMatchers.ClassicGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    EXT_FGT_S("FineGrainedTypesWithSimple",(benchmarkCase, query) ->
            new GumTreeWithTreeModifier(
                    new BlockAndSimpleNameModifier(),
                    new CompositeMatchers.SimpleGumtree(), query.apply(benchmarkCase.getProjectASTDiff())))
    ,
    EXT_FGT_STM_NMS_G("P1G", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new BlockAndSimpleNameModifier(),
                            new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                            new NonMatchedSubtreesAdditionalRound(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.ClassicGumtree())))),
    EXT_FGT_SVN_NMS_G("P2G", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new BlockAndSimpleNameModifier(),
                            new SingleVirtualNodeMatching(),
                            new NonMatchedSubtreesAdditionalRound(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.ClassicGumtree()))))
    ,
    EXT_FGT_STM_NMS_S("P1S", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new BlockAndSimpleNameModifier(),
                            new StagedTreeMatching(benchmarkCase.getProjectASTDiff()),
                            new NonMatchedSubtreesAdditionalRound(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.SimpleGumtree())))),
    EXT_FGT_SVN_NMS_S("P2S", ((benchmarkCase, query) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new BlockAndSimpleNameModifier(),
                            new SingleVirtualNodeMatching(),
                            new NonMatchedSubtreesAdditionalRound(),
                            benchmarkCase,
                            query,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    SPN_I("Original Spoon (Incompatible)", Spoon::new),

    SPN___("Spoon Compatible (Before applying translation rules)", (benchmarkCase, query) -> new SpoonWithOffsetTranslation(benchmarkCase, query, false)),

//    SPN_OFFSET_TRANSLATED_WITH_RULES("Spoon Compatible (After applying translation rules)", SpoonWithOffsetTranslation::new),

    SPN_T("Spoon", FinalizedSpoon::new),

    SPN_G_T("Spoon Greedy Matcher (Same matcher configuration as GumTree Greedy)",
            (benchmarkCase, query) -> {
                DiffConfiguration configuration = makeSpoonCopyConfiguration(
                        new shadedspoon.com.github.gumtreediff.matchers.CompositeMatchers.ClassicGumtree()
                );
                return new FinalizedSpoon(benchmarkCase, query, configuration);
            }),

    SPN_G_("Spoon Greedy Incompatible (Same matcher configuration as GumTree Greedy)",
            (benchmarkCase, query) -> {
                DiffConfiguration configuration = makeSpoonCopyConfiguration(
                        new shadedspoon.com.github.gumtreediff.matchers.CompositeMatchers.ClassicGumtree()
                );
                return new Spoon(benchmarkCase, query, configuration);
            }),

    SPN_S_T("Spoon Simple Matcher (Same matcher configuration as GumTree Simple)",
            (benchmarkCase, query) -> {
                DiffConfiguration configuration = makeSpoonCopyConfiguration(
                        new shadedspoon.com.github.gumtreediff.matchers.CompositeMatchers.SimpleGumtree()
                );
                return new FinalizedSpoon(benchmarkCase, query, configuration);
            }),
    ;

    private static DiffConfiguration makeSpoonCopyConfiguration(shadedspoon.com.github.gumtreediff.matchers.CompositeMatchers.CompositeMatcher matcher) {
        DiffConfiguration configuration = new DiffConfiguration();
        configuration.setMatcher(matcher);
        configuration.setGumtreeProperties(new GumtreeProperties());
        return configuration;
    }

    private final ASTDiffProviderForBenchmark factory;
    private final String toolName;

    ASTDiffToolEnum(String toolName, ASTDiffProviderForBenchmark factory) {
        this.factory = factory;
        this.toolName = toolName;
    }


    public String getToolName() {
        return toolName;
    }
    public String getShortName() {
        return this.name();
    }

    @Override
    public ASTDiffProvider apply(IBenchmarkCase benchmarkCase, DiffSelector query) {
        return factory.apply(benchmarkCase, query);
    }

    public ASTDiff diff(IBenchmarkCase benchmarkCase, DiffSelector querySelector) throws Exception {
        return factory.apply(benchmarkCase, querySelector).getASTDiff();
    }
}



