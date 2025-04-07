package benchmark.generators.tools.runners.shaded;


import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.models.selector.DiffSelector;


/* Created by pourya on 2023-04-17 7:58 p.m. */
public class IJM extends AbstractASTDiffProviderFromIncompatibleTree {
    public IJM(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
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
