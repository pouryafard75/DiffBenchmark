package benchmark.utils;

import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import org.refactoringminer.astDiff.models.ASTDiff;


/* Created by pourya on 2023-04-17 8:53 p.m. */
public class Helpers {
    public static Diff diffByGumTree(ASTDiff astDiff, CompositeMatchers.CompositeMatcher matcher) {
        MappingStore match = matcher.match(astDiff.src.getRoot(), astDiff.dst.getRoot());
        EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
        return new Diff(astDiff.src, astDiff.dst, match, actions);
    }
}
