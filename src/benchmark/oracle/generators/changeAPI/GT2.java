package benchmark.oracle.generators.changeAPI;

import at.aau.softwaredynamics.gen.DocIgnoringTreeGenerator;
import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.gen.SpoonTreeGenerator;
import com.github.gumtreediff.gen.TreeGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import shaded.com.github.gumtreediff.matchers.CompositeMatchers;
import shaded.com.github.gumtreediff.matchers.Matcher;

/* Created by pourya on 2023-05-17 6:05 p.m. */
public class GT2 extends APIChanger {
    public GT2(ProjectASTDiff projectASTDiff, ASTDiff astDiff) {
        super(projectASTDiff, astDiff);
    }
    @Override
    public Class<? extends Matcher> getMatcherType() {
        return CompositeMatchers.ClassicGumtree.class;
    }
    @Override
    public shaded.com.github.gumtreediff.gen.TreeGenerator getGeneratorType() {
        return new OptimizedJdtTreeGenerator();
    }
}
