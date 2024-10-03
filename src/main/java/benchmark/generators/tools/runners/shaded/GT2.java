package benchmark.generators.tools.runners.shaded;

import benchmark.data.diffcase.IBenchmarkCase;

import benchmark.utils.Experiments.IQuerySelector;
import org.refactoringminer.astDiff.models.ASTDiff;
import shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.CompositeMatchers;
import shaded.com.github.gumtreediff.matchers.Matcher;

/* Created by pourya on 2023-05-17 6:05 p.m. */
public class GT2 extends AbstractASTDiffProviderFromIncompatibleTree {
    public GT2(IBenchmarkCase benchmarkCase, IQuerySelector querySelector) {
        super(benchmarkCase, querySelector);
    }

    @Override
    public Class<? extends Matcher> getMatcherType() {
        return CompositeMatchers.ClassicGumtree.class;
    }
    @Override
    public shaded.com.github.gumtreediff.gen.TreeGenerator getGeneratorType() {
        return new JdtTreeGenerator();
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
        return diffWithTrivialAddition();
    }
}
