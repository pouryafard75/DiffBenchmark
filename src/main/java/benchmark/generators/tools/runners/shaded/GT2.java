package benchmark.generators.tools.runners.shaded;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.CompositeMatchers;
import shaded.com.github.gumtreediff.matchers.Matcher;

/* Created by pourya on 2023-05-17 6:05 p.m. */
public class GT2 extends AbstractASTDiffProviderFromIncompatibleTree {
    public GT2(ProjectASTDiff projectASTDiff, ASTDiff astDiff, CaseInfo info, Configuration configuration) {
        super(projectASTDiff, astDiff, info, configuration);
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
        return diffWithTrivialAddition(info, conf);
    }
}
