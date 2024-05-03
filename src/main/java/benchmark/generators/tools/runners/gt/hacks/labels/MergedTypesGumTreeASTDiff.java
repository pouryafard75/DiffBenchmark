package benchmark.generators.tools.runners.gt.hacks.labels;

import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TypeSet;
import org.refactoringminer.astDiff.actions.ASTDiff;

import java.util.function.BiConsumer;
import java.util.function.Function;

/* Created by pourya on 2024-05-02*/
public class MergedTypesGumTreeASTDiff extends BaseMergedNodesGumTreeASTDiffProvider {
    private static final Function<Tree, String> getType = (tree) -> tree.getType().name;
    private static final BiConsumer<Tree, String> setType = (tree, input) -> tree.setType(TypeSet.type(input));
    public MergedTypesGumTreeASTDiff(CompositeMatchers.CompositeMatcher matcher, ASTDiff input) {
        super(matcher, input, getType, setType);
    }
}
