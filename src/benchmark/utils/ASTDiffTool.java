package benchmark.utils;

import benchmark.metrics.models.TrivialDiff;
import benchmark.oracle.generators.PerfectDiff;
import benchmark.oracle.generators.changeAPI.APIChanger;
import benchmark.oracle.generators.changeAPI.GT2;
import benchmark.oracle.generators.changeAPI.IJM;
import benchmark.oracle.generators.changeAPI.MTDiff;
import com.github.gumtreediff.matchers.CompositeMatchers;

public enum ASTDiffTool {
    GOD ((projectASTDiff, rm_astDiff, info, configuration) -> {
        try {
            return new PerfectDiff(projectASTDiff, rm_astDiff,info,configuration).makeASTDiff();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    })
    ,
    RMD ((projectASTDiff, rm_astDiff, info, configuration) ->  rm_astDiff)
    ,
    GTG ((projectASTDiff, rm_astDiff, info, configuration) -> APIChanger.makeASTDiffFromMatcher(new CompositeMatchers.ClassicGumtree(),rm_astDiff))
    ,
    GTS ((projectASTDiff, rm_astDiff, info, configuration) -> APIChanger.makeASTDiffFromMatcher(new CompositeMatchers.SimpleGumtree(), rm_astDiff))
    ,
    IJM ((projectASTDiff, rm_astDiff, info, configuration) -> {
        try {
            return new IJM(projectASTDiff,rm_astDiff).makeASTDiff();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    })
    ,
    MTD ((projectASTDiff, rm_astDiff, info, configuration) -> {
        try {
            return new MTDiff(projectASTDiff,rm_astDiff).makeASTDiff();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    })
    ,
    GT2 ((projectASTDiff, rm_astDiff, info, configuration) -> {
        try {
            return new GT2(projectASTDiff,rm_astDiff).makeASTDiff();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    })
    ,
    TRV ((projectASTDiff, rm_astDiff, info, configuration) ->  new TrivialDiff(rm_astDiff).makeASTDiff());

    private final DiffToolFactory factory;
    public DiffToolFactory getFactory() {
        return factory;
    }
    ASTDiffTool(DiffToolFactory factory) {
        this.factory = factory;
    }
}


