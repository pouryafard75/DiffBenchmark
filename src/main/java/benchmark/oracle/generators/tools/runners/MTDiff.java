package benchmark.oracle.generators.tools.runners;


import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import shaded.com.github.gumtreediff.gen.TreeGenerator;
import shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator;


/* Created by pourya on 2023-04-17 7:59 p.m. */
public class MTDiff extends APIChanger {

    private TreeGenerator generator = new JdtTreeGenerator();

    public MTDiff(ProjectASTDiff projectASTDiff, ASTDiff astDiff) {
        super(projectASTDiff, astDiff);
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
}