package benchmark.generators.tools.runners.shaded;


import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import shaded.com.github.gumtreediff.gen.TreeGenerator;
import shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator;


/* Created by pourya on 2023-04-17 7:59 p.m. */
public class MTDiff extends AbstractASTDiffProviderFromIncompatibleTree {

    private final CaseInfo info;
    private final Configuration configuration;
    private TreeGenerator generator = new JdtTreeGenerator();

    public MTDiff(ProjectASTDiff projectASTDiff, ASTDiff astDiff, CaseInfo info, Configuration conf) {
        super(projectASTDiff, astDiff);
        this.info = info;
        this.configuration = conf;
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
        return diffWithTrivialAddition(info, configuration);
    }
}
