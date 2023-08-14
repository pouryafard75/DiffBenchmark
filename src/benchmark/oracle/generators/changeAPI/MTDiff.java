package benchmark.oracle.generators.changeAPI;


import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;


/* Created by pourya on 2023-04-17 7:59 p.m. */
public class MTDiff extends APIChanger {
    public MTDiff(ProjectASTDiff projectASTDiff, ASTDiff astDiff) {
        super(projectASTDiff, astDiff);
    }
    @Override
    public Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType() {
        return shaded.com.github.gumtreediff.matchers.OptimizedVersions.MtDiff.class;
    }
}
