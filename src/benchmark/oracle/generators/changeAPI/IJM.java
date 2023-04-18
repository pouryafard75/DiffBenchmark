package benchmark.oracle.generators.changeAPI;

import org.refactoringminer.astDiff.actions.ASTDiff;


/* Created by pourya on 2023-04-17 7:58 p.m. */
public class IJM extends APIChanger {
    public IJM(ASTDiff astDiff) {
        super(astDiff);
    }
    @Override
    public Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType() {
        return at.aau.softwaredynamics.matchers.JavaMatchers.IterativeJavaMatcher_V2.class;
    }

}
