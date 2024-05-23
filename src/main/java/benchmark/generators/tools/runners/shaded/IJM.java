package benchmark.generators.tools.runners.shaded;


import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;


/* Created by pourya on 2023-04-17 7:58 p.m. */
public class IJM extends AbstractASTDiffProviderFromIncompatibleTree {
    public IJM(ProjectASTDiff projectASTDiff, ASTDiff astDiff) {
        super(projectASTDiff, astDiff);
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
