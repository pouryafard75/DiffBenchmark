package benchmark.generators.tools;

import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.ASTDiffProviderFactory;
import benchmark.generators.tools.runners.IASTMapper;
import benchmark.generators.tools.runners.converter.PerfectDiff;
import benchmark.generators.tools.runners.gt.GreedyGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.SimpleGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.hacks.all.ModifierInterConservativeMulti;
import benchmark.generators.tools.runners.hacks.all.ModifierInterNoMulti;
import benchmark.generators.tools.runners.hacks.interfile.ProjectGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.hacks.interfile.ProjectGumTreeOptimizer;
import benchmark.generators.tools.runners.hacks.interfile.SingleVirtualNodeMatching;
import benchmark.generators.tools.runners.hacks.interfile.StagedTreeMatching;
import benchmark.generators.tools.runners.hacks.labels.GumTreeWithTreeModifier;
import benchmark.generators.tools.runners.hacks.labels.LeafLabelMerger;
import benchmark.generators.tools.runners.hacks.labels.LeafTypeMerger;
import benchmark.generators.tools.runners.hacks.multimapping.CopyPaste;
import benchmark.generators.tools.runners.hacks.multimapping.GumTreeWithMultiMappingASTDiffProvider;
import benchmark.generators.tools.runners.hacks.multimapping.NonMatchedSubtreesAdditionalRound;
import benchmark.generators.tools.runners.shaded.IJM;
import benchmark.generators.tools.runners.shaded.MTDiff;
import benchmark.generators.tools.runners.trivial.TrivialDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;


public enum ASTDiffTool {
    GOD ("PerfectDiff", PerfectDiff::new),

    RMD ("RefactoringMiner", (projectASTDiff, input, info, configuration) -> (ASTDiffProvider) () -> input),

    GTG ("GumTree Greedy 3.0", (projectASTDiff, input, info, configuration) -> new GreedyGumTreeASTDiffProvider(input)),

    GTS ("GumTree Simple 3.0", (projectASTDiff, input, info, configuration) -> new SimpleGumTreeASTDiffProvider(input)),

    IJM ("Iterative Java Matcher", (projectASTDiff, input, info, configuration) -> new IJM(projectASTDiff,input))
    ,
    MTD ("MoveOptimized Tree Differencing", MTDiff::new)
    ,
    GT2 ("GumTree 2.0", benchmark.generators.tools.runners.shaded.GT2::new)
    ,
    IAM ("IASTMapper", (projectASTDiff, input, info, configuration) -> new IASTMapper(projectASTDiff,input))
    ,
    DAT ("Diff Auto Tuning", null)
    ,
    RM2 ("RefactoringMiner 2.0", benchmark.generators.tools.runners.converter.RM2::new)
    ,
    TRV ("TrivialDiff", TrivialDiff::new)
    ,
    VNG ("VirtualNodeWithGreedy", (projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        projectASTDiff, input, info, configuration,
                        new CompositeMatchers.ClassicGumtree())))
    ,
    VNS ("VirtualNodeWithSimple", (projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new SingleVirtualNodeMatching(),
                        projectASTDiff, input, info, configuration,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    SMG ("StagedTreeMatchingGreedy", (projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(projectASTDiff),
                        projectASTDiff, input, info, configuration,
                        new CompositeMatchers.ClassicGumtree())))

    ,
    SMS ("StagedTreeMatchingSimple", (projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                new ProjectGumTreeASTDiffProvider(
                        new StagedTreeMatching(projectASTDiff),
                        projectASTDiff, input, info, configuration,
                        new CompositeMatchers.SimpleGumtree())))
    ,
    NMG ("NonMappedSubTreesWithGreedy", (projectASTDiff, input, info, configuration) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.ClassicGumtree(), input))
    ,
    NMS ("NonMappedSubTreesWithSimple", (projectASTDiff, input, info, configuration) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new NonMatchedSubtreesAdditionalRound(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    CPG("CopyPasteWithGreedy", (projectASTDiff, input, info, configuration) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.ClassicGumtree(), input))
    ,
    CPS ("CopyPasteWithSimple", (projectASTDiff, input, info, configuration) ->
            new GumTreeWithMultiMappingASTDiffProvider(
                    new CopyPaste(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    FLG ("FineGrainedLabelsWithGreedy", (projectASTDiff, input, info, configuration) ->
            new GumTreeWithTreeModifier(
                    new LeafLabelMerger(),
                    new CompositeMatchers.ClassicGumtree(), input
            ))
    ,
    FLS ("FineGrainedLabelsWithSimple", (projectASTDiff, input, info, configuration) ->
            new GumTreeWithTreeModifier(
                    new LeafLabelMerger(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    FTG ("FineGrainedTypesWithGreedy", (projectASTDiff, input, info, configuration) ->
            new GumTreeWithTreeModifier(
                    new LeafTypeMerger(),
                    new CompositeMatchers.ClassicGumtree(), input))
    ,
    FTS ("FineGrainedTypesWithSimple",(projectASTDiff, input, info, configuration) ->
            new GumTreeWithTreeModifier(
                    new LeafTypeMerger(),
                    new CompositeMatchers.SimpleGumtree(), input))
    ,
    COMBINED_TYPE_STAGED_GREEDY ("COMBINED_TYPE_STAGED_GREEDY", ((projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            projectASTDiff, input, info, configuration,
                            new CompositeMatchers.ClassicGumtree())))),
    COMBINED_TYPE_STAGED_SIMPLE ("COMBINED_TYPE_STAGED_SIMPLE", ((projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            projectASTDiff, input, info, configuration,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    COMBINED_TYPE_VN_GREEDY ("COMBINED_TYPE_SVN_GREEDY", ((projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new SingleVirtualNodeMatching(),
                            projectASTDiff, input, info, configuration,
                            new CompositeMatchers.ClassicGumtree())))),
    COMBINED_TYPE_VN_SIMPLE ("COMBINED_TYPE_SVN_SIMPLE", ((projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterNoMulti(
                            new LeafTypeMerger(),
                            new SingleVirtualNodeMatching(),
                            projectASTDiff, input, info, configuration,
                            new CompositeMatchers.SimpleGumtree()))))
    ,
    X_TYPE_STAGED_NONMATCHED_GREEDY ("MERGED_STAGED_NONMATCHED_GREEDY", ((projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            new NonMatchedSubtreesAdditionalRound(),
                            projectASTDiff, input, info, configuration,
                            new CompositeMatchers.ClassicGumtree()))))
    ,
    X_TYPE_STAGED_NONMATCHED_SIMPLE ("MERGED_STAGED_NONMATCHED_SIMPLE", ((projectASTDiff, input, info, configuration) ->
            new ProjectGumTreeOptimizer(
                    new ModifierInterConservativeMulti(
                            new LeafTypeMerger(),
                            new StagedTreeMatching(projectASTDiff),
                            new NonMatchedSubtreesAdditionalRound(),
                            projectASTDiff, input, info, configuration,
                            new CompositeMatchers.SimpleGumtree()))))
    ,


    ;

    private final ASTDiffProviderFactory factory;
    private final String toolName;
    public ASTDiff diff(ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo info, Configuration configuration) throws Exception {
        return factory.getASTDiffer(projectASTDiff, input, info, configuration).makeASTDiff();
    }
    ASTDiffTool(String toolName, ASTDiffProviderFactory factory) {
        this.factory = factory;
        this.toolName = toolName;
    }

    public String getToolName() {
        return toolName;
    }
}



