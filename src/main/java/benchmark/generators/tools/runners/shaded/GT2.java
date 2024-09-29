package benchmark.generators.tools.runners.shaded;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.ExperimentConfiguration;
import benchmark.data.exp.IExperiment;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.CompositeMatchers;
import shaded.com.github.gumtreediff.matchers.Matcher;

/* Created by pourya on 2023-05-17 6:05 p.m. */
public class GT2 extends AbstractASTDiffProviderFromIncompatibleTree {
    public GT2(ProjectASTDiff projectASTDiff, ASTDiff astDiff, BenchmarkCase info, IExperiment experiment) {
        super(projectASTDiff, astDiff, info, experiment);
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
    public ASTDiff makeASTDiff() throws Exception {
        return diffWithTrivialAddition(info, experiment);
    }
}
