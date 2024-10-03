package benchmark.generators.tools.runners.shaded;


import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.utils.Experiments.IQuerySelector;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;


/* Created by pourya on 2023-04-17 7:58 p.m. */
public class IJM extends AbstractASTDiffProviderFromIncompatibleTree {
    public IJM(IBenchmarkCase benchmarkCase, IQuerySelector querySelector) {
        super(benchmarkCase, querySelector);
    }

    @Override
    public Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType() {
        return at.aau.softwaredynamics.matchers.JavaMatchers.IterativeJavaMatcher_V2.class;
    }
    @Override
    public shaded.com.github.gumtreediff.gen.TreeGenerator getGeneratorType() {
        return new OptimizedJdtTreeGenerator();
    }


}
