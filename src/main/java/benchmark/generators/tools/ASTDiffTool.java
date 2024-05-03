package benchmark.generators.tools;

import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.ASTDiffProviderFactory;
import benchmark.generators.tools.runners.IASTMapper;
import benchmark.generators.tools.runners.converter.PerfectDiff;
import benchmark.generators.tools.runners.gt.GreedyGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.SimpleGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.hacks.interfile.EfficientProjectGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.hacks.interfile.SingleVirtualNodeGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.hacks.interfile.StagedTreeMatchingGumTreeASTDiffProvider;
import benchmark.generators.tools.runners.gt.hacks.labels.MergedLabelsGumTreeASTDiff;
import benchmark.generators.tools.runners.gt.hacks.labels.MergedTypesGumTreeASTDiff;
import benchmark.generators.tools.runners.gt.hacks.multimapping.CopyPaste;
import benchmark.generators.tools.runners.shaded.IJM;
import benchmark.generators.tools.runners.shaded.MTDiff;
import benchmark.generators.tools.runners.trivial.TrivialDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;


public enum ASTDiffTool {

    GOD (PerfectDiff::new)
    ,
    RMD ((projectASTDiff, input, info, configuration) -> (ASTDiffProvider) () -> input)
    ,
    GTG ((projectASTDiff, input, info, configuration) -> new GreedyGumTreeASTDiffProvider(input))
    ,
    GTS ((projectASTDiff, input, info, configuration) -> new SimpleGumTreeASTDiffProvider(input))
    ,
    IJM ((projectASTDiff, input, info, configuration) -> new IJM(projectASTDiff,input))
    ,
    MTD (MTDiff::new)
    ,
    GT2 (benchmark.generators.tools.runners.shaded.GT2::new)
    ,
    IAM((projectASTDiff, input, info, configuration) -> new IASTMapper(projectASTDiff,input))
    ,
    DAT(null)
    ,
    RM2(benchmark.generators.tools.runners.converter.RM2::new)
    ,
    TRV (TrivialDiff::new)
    ,
    SingleVirtualNode ((projectASTDiff, input, info, configuration) -> new EfficientProjectGumTreeASTDiffProvider(new SingleVirtualNodeGumTreeASTDiffProvider(projectASTDiff, input, info, configuration, new CompositeMatchers.ClassicGumtree())))
    ,
    SingleVirtualNodeWithSimple ((projectASTDiff, input, info, configuration) -> new EfficientProjectGumTreeASTDiffProvider(new SingleVirtualNodeGumTreeASTDiffProvider(projectASTDiff, input, info, configuration, new CompositeMatchers.SimpleGumtree())))
    ,
    StagedTreeMatching ((projectASTDiff, input, info, configuration) -> new EfficientProjectGumTreeASTDiffProvider(new StagedTreeMatchingGumTreeASTDiffProvider(projectASTDiff, input, info, configuration, new CompositeMatchers.ClassicGumtree())))
    ,
    StagedTreeMatchingWithSimple ((projectASTDiff, input, info, configuration) -> new EfficientProjectGumTreeASTDiffProvider(new StagedTreeMatchingGumTreeASTDiffProvider(projectASTDiff, input, info, configuration, new CompositeMatchers.SimpleGumtree())))
    ,
    CopyPaste ((projectASTDiff, input, info, configuration) -> new CopyPaste(new CompositeMatchers.ClassicGumtree(), input))
    ,
    CopyPasteWithSimple ((projectASTDiff, input, info, configuration) -> new CopyPaste(new CompositeMatchers.SimpleGumtree(), input))
    ,
    FineGrainedLabels ((projectASTDiff, input, info, configuration) -> new MergedLabelsGumTreeASTDiff(new CompositeMatchers.ClassicGumtree(), input))
    ,
    FineGrainedLabelsWithSimple ((projectASTDiff, input, info, configuration) -> new MergedLabelsGumTreeASTDiff(new CompositeMatchers.SimpleGumtree(), input))
    ,
    FineGrainedTypes ((projectASTDiff, input, info, configuration) -> new MergedTypesGumTreeASTDiff(new CompositeMatchers.ClassicGumtree(), input))
    ,
    FineGrainedTypesWithSimple ((projectASTDiff, input, info, configuration) -> new MergedTypesGumTreeASTDiff(new CompositeMatchers.SimpleGumtree(), input))
    ,
    AllHacks((projectASTDiff, input, info, configuration) -> null /*TODO*/)
    ,
    AllHacksWithSimple((projectASTDiff, input, info, configuration) -> null /*TODO*/)
    ,

    ;

    private final ASTDiffProviderFactory factory;
    public ASTDiff diff(ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo info, Configuration configuration) throws Exception {
        return factory.getASTDiffer(projectASTDiff, input, info, configuration).makeASTDiff();
    }
    ASTDiffTool(ASTDiffProviderFactory factory) {
        this.factory = factory;
    }
}



