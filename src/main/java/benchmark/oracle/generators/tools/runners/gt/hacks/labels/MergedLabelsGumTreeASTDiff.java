package benchmark.oracle.generators.tools.runners.gt.hacks.labels;

import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;

import java.util.function.BiConsumer;
import java.util.function.Function;

/* Created by pourya on 2024-05-02*/
public class MergedLabelsGumTreeASTDiff extends BaseMergedNodesGumTreeASTDiffProvider {
    private static final Function<Tree, String> getLabel = Tree::getLabel;
    private static final BiConsumer<Tree, String> setLabel = Tree::setLabel;
    public MergedLabelsGumTreeASTDiff(CompositeMatchers.CompositeMatcher matcher, ASTDiff input) {
        super(matcher, input, getLabel, setLabel);
    }
}
