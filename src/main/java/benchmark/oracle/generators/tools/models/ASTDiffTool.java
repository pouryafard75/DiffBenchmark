package benchmark.oracle.generators.tools.models;

import benchmark.oracle.generators.tools.runners.*;
import com.github.gumtreediff.matchers.CompositeMatchers;

public enum ASTDiffTool {
    GOD ((projectASTDiff, rm_astDiff, info, configuration) -> new PerfectDiff(projectASTDiff, rm_astDiff,info,configuration).makeASTDiff())
    ,
    RMD ((projectASTDiff, rm_astDiff, info, configuration) ->  rm_astDiff)
    ,
    GTG ((projectASTDiff, rm_astDiff, info, configuration) -> APIChanger.makeASTDiffFromMatcher(new CompositeMatchers.ClassicGumtree(),rm_astDiff))
    ,
    GTS ((projectASTDiff, rm_astDiff, info, configuration) -> APIChanger.makeASTDiffFromMatcher(new CompositeMatchers.SimpleGumtree(), rm_astDiff))
    ,
    IJM ((projectASTDiff, rm_astDiff, info, configuration) -> new IJM(projectASTDiff,rm_astDiff).makeASTDiff())
    ,
    MTD ((projectASTDiff, rm_astDiff, info, configuration) -> new MTDiff(projectASTDiff,rm_astDiff, info, configuration).makeASTDiff())
    ,
    GT2 ((projectASTDiff, rm_astDiff, info, configuration) -> new GT2(projectASTDiff,rm_astDiff, info, configuration).makeASTDiff())
    ,
    IAM((projectASTDiff, rm_astDiff, info, configuration) -> new iASTMapper(projectASTDiff,rm_astDiff).makeASTDiff())
    ,
    DAT(null),

    RM2((projectASTDiff, rm_astDiff, info, configuration) ->  new RM2(projectASTDiff, rm_astDiff,info, configuration).makeASTDiff()),

    TRV ((projectASTDiff, rm_astDiff, info, configuration) -> new TrivialDiff(projectASTDiff, rm_astDiff, info, configuration).makeASTDiff()),

    OBV ((projectASTDiff, rm_astDiff, info, configuration) -> new EfficentCommitLevelGumTreeASTDiff(projectASTDiff, rm_astDiff, info, configuration, new CompositeMatchers.ClassicGumtree()).makeASTDiff());

    ;


    private final DiffToolFactory factory;
    public DiffToolFactory getFactory() {
        return factory;
    }
    ASTDiffTool(DiffToolFactory factory) {
        this.factory = factory;
    }
}



