package benchmark.oracle.generators.changeAPI;

import org.refactoringminer.astDiff.actions.ASTDiff;
import shaded.com.github.gumtreediff.matchers.CompositeMatchers;
import shaded.com.github.gumtreediff.matchers.Matcher;

/* Created by pourya on 2023-05-17 6:05 p.m. */
public class GT2 extends APIChanger {
    public GT2(ASTDiff astDiff) {
        super(astDiff);
    }
    @Override
    public Class<? extends Matcher> getMatcherType() {
        return CompositeMatchers.ClassicGumtree.class;
    }
}
