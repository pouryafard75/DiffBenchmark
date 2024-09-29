package benchmark.generators.tools;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.ASTDiffProviderFactory;
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
import benchmark.generators.tools.runners.shaded.GT2;
import benchmark.generators.tools.runners.shaded.IJM;
import benchmark.generators.tools.runners.shaded.MTDiff;
import benchmark.generators.tools.runners.trivial.TrivialDiff;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;


public enum ASTDiffTool implements IASTDiffTool {
    GOD ("PerfectDiff", PerfectDiff::new),

    RMD ("RefactoringMiner", (projectASTDiff, input, info) -> () -> input),

    GTG ("GumTree Greedy 3.0", (projectASTDiff, input, info) -> new GreedyGumTreeASTDiffProvider(input)),

    GTS ("GumTree Simple 3.0", (projectASTDiff, input, info) -> new SimpleGumTreeASTDiffProvider(input)),

    IJM ("Iterative Java Matcher", (projectASTDiff, input, info) -> new IJM(projectASTDiff,input))
    ,
    MTD ("MoveOptimized Tree Differencing", (projectASTDiff, astDiff, info) -> new MTDiff(projectASTDiff, astDiff, info))
    ,
    GT2 ("GumTree 2.0", (projectASTDiff, astDiff, info) -> new GT2(projectASTDiff, astDiff, info))
    ,
    IAM ("IASTMapper", (projectASTDiff, input, info) -> new IASTMapper(projectASTDiff,input))
    ,
    DAT ("Diff Auto Tuning", null)
    ,
    RM2 ("RefactoringMiner 2.0", benchmark.generators.tools.runners.converter.RM2::new)
    ,
    TRV ("TrivialDiff", TrivialDiff::new)
    ,
    VNG ("VirtualNodeWithGreedy", (projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        projectASTDiff, input, info,
                        new CompositeMatchers.ClassicGumtree())))
    ,
    VNS ("VirtualNodeWithSimple", (projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        projectASTDiff, input, info,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    SMG ("StagedTreeMatchingGreedy", (projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(projectASTDiff),
                        projectASTDiff, input, info,
                        new CompositeMatchers.ClassicGumtree())))

    ,
    SMS ("StagedTreeMatchingSimple", (projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(projectASTDiff),
                        projectASTDiff, input, info,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    NMG ("NonMappedSubTreesWithGreedy", (projectASTDiff, input, info) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.ClassicGumtree(), input))
    ,
    NMS ("NonMappedSubTreesWithSimple", (projectASTDiff, input, info) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    CPG("CopyPasteWithGreedy", (projectASTDiff, input, info) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.ClassicGumtree(), input))
    ,
    CPS ("CopyPasteWithSimple", (projectASTDiff, input, info) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    FLG ("FineGrainedLabelsWithGreedy", (projectASTDiff, input, info) ->
            new GumTreeWithTreeModifier(
                    new LeafLabelMerger(),
                    new CompositeMatchers.ClassicGumtree(), input
            ))
    ,
    FLS ("FineGrainedLabelsWithSimple", (projectASTDiff, input, info) ->
            new GumTreeWithTreeModifier(
                    new LeafLabelMerger(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    FTG ("FineGrainedTypesWithGreedy", (projectASTDiff, input, info) ->
            new GumTreeWithTreeModifier(
                    new LeafTypeMerger(),
                    new CompositeMatchers.ClassicGumtree(), input))
    ,
    FTS ("FineGrainedTypesWithSimple",(projectASTDiff, input, info) ->
            new GumTreeWithTreeModifier(
                    new LeafTypeMerger(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    COMBINED_TYPE_STAGED_GREEDY ("COMBINED_TYPE_STAGED_GREEDY", ((projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            projectASTDiff, input, info,
                            new CompositeMatchers.ClassicGumtree())))),
    COMBINED_TYPE_STAGED_SIMPLE ("COMBINED_TYPE_STAGED_SIMPLE", ((projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            projectASTDiff, input, info,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    COMBINED_TYPE_VN_GREEDY ("COMBINED_TYPE_SVN_GREEDY", ((projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new SingleVirtualNodeMatching(),
                            projectASTDiff, input, info,
                            new CompositeMatchers.ClassicGumtree())))),
    COMBINED_TYPE_VN_SIMPLE ("COMBINED_TYPE_SVN_SIMPLE", ((projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new SingleVirtualNodeMatching(),
                            projectASTDiff, input, info,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    X_TYPE_STAGED_NONMATCHED_GREEDY ("MERGED_STAGED_NONMATCHED_GREEDY", ((projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            new NonMatchedSubtreesAdditionalRound(),
                            projectASTDiff, input, info,
                            new CompositeMatchers.ClassicGumtree()))))
    ,
    X_TYPE_STAGED_NONMATCHED_SIMPLE ("MERGED_STAGED_NONMATCHED_SIMPLE", ((projectASTDiff, input, info) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            new NonMatchedSubtreesAdditionalRound(),
                            projectASTDiff, input, info,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    SPN ("Spoon", Spoon::new)

    ;

    private final ASTDiffProviderFactory factory;
    private final String toolName;
    public ASTDiff diff(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info) throws Exception {
        return factory.getASTDiffer(projectASTDiff, input, info).makeASTDiff();
    }
    ASTDiffTool(String toolName, ASTDiffProviderFactory factory) {
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
    public ASTDiffProvider getASTDiffer(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info) throws Exception {
        return factory.getASTDiffer(projectASTDiff, input, info);
    }
}



