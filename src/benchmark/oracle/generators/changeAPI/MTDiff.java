package benchmark.oracle.generators.changeAPI;


import org.refactoringminer.astDiff.actions.ASTDiff;


/* Created by pourya on 2023-04-17 7:59 p.m. */
public class MTDiff extends APIChanger {
    public MTDiff(ASTDiff astDiff) {
        super(astDiff);
    }
    @Override
    public Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType() {
        return shaded.com.github.gumtreediff.matchers.OptimizedVersions.MtDiff.class;
    }
}
