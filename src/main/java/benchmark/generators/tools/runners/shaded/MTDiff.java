package benchmark.generators.tools.runners.shaded;


import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.ExperimentConfiguration;
import benchmark.data.exp.IExperiment;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import shaded.com.github.gumtreediff.gen.TreeGenerator;
import shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator;


/* Created by pourya on 2023-04-17 7:59 p.m. */
public class MTDiff extends AbstractASTDiffProviderFromIncompatibleTree {

    private final BenchmarkCase info;
    private final IExperiment experiment;
    private TreeGenerator generator = new JdtTreeGenerator();

    public MTDiff(ProjectASTDiff projectASTDiff, ASTDiff astDiff, BenchmarkCase info, IExperiment experiment) {
        super(projectASTDiff, astDiff);
        this.info = info;
        this.experiment = experiment;
        if (astDiff.getSrcPath().equals("core/src/processing/core/PApplet.java")) //Since this case cause the java heap space, we decided to run this case with the OptimizedJDTGenerator
            generator = new OptimizedJdtTreeGenerator();

    }
    @Override
    public Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType() {
        return shaded.com.github.gumtreediff.matchers.OptimizedVersions.MtDiff.class;
    }
    @Override
    public shaded.com.github.gumtreediff.gen.TreeGenerator getGeneratorType() {
        return generator;
    }

    @Override
    public ASTDiff makeASTDiff() throws Exception {
        return diffWithTrivialAddition(info, experiment);
    }
}
