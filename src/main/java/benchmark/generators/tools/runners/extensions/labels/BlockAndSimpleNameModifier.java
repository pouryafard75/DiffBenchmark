package benchmark.generators.tools.runners.extensions.labels;

import com.github.gumtreediff.tree.FakeTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeUtils;
import com.github.gumtreediff.tree.TypeSet;
import org.refactoringminer.astDiff.utils.Constants;

import java.util.List;

/* Created by pourya on 2025-02-07*/
public class BlockAndSimpleNameModifier implements TreeModifier {

    List<String> SEARCHING_TYPES = List.of(
            Constants.BLOCK,
            Constants.SIMPLE_NAME);
    @Override
    public void modify(Tree baseTree) {
        for (Tree tree : TreeUtils.breadthFirst(baseTree)) {
            if (tree instanceof FakeTree) continue;
            if (SEARCHING_TYPES.contains(tree.getType().name)) {
                tree.setType(TypeSet.type(
                                tree.getType().name + "_" + tree.getParent().getType().name
                        ));
            }
        }
    }
}
