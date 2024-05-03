package benchmark.generators.tools.runners.gt;

import com.github.gumtreediff.matchers.CompositeMatchers;
import org.refactoringminer.astDiff.actions.ASTDiff;

/* Created by pourya on 2024-05-02*/
public class GreedyGumTreeASTDiffProvider extends BaseGumTreeASTDiffProvider {
    public GreedyGumTreeASTDiffProvider(ASTDiff rmAstDiff) {
        super(new CompositeMatchers.ClassicGumtree(), rmAstDiff);
    }
}
