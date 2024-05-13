package benchmark.generators.tools.runners.hacks.labels;

import com.github.gumtreediff.tree.FakeTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeUtils;

import java.util.function.BiConsumer;
import java.util.function.Function;

/* Created by pourya on 2024-05-12*/
public abstract class AbstractLeafMergerTreeModifier implements TreeModifier {
    private final BiConsumer<Tree, String> setFunction;
    private final Function<Tree, String> getFunction;
    public AbstractLeafMergerTreeModifier(Function<Tree, String> getFunction, BiConsumer<Tree, String> setFunction) {
        this.setFunction = setFunction;
        this.getFunction = getFunction;
    }

    public void modify(Tree baseTree){
        mergeLeavesAndSingleChildInnersWithFunctions(baseTree, getFunction, setFunction);
    }
    protected void mergeLeavesAndSingleChildInnersWithFunctions(Tree baseTree, Function<Tree, String> getFunction, BiConsumer<Tree, String> setFunction) {
        for (Tree tree : TreeUtils.breadthFirst(baseTree)) {
            if (tree instanceof FakeTree) continue;
            if (tree.isLeaf()){
                setFunction.accept(tree, getFunction.apply(tree.getParent()) + "_" + getFunction.apply(tree));
            }
            else if (tree.getChildren().size() == 1) {
                setFunction.accept(tree, getFunction.apply(tree) + "_" + getFunction.apply(tree.getChildren().get(0)));
            }
            else {
                //Do nothing
            }
        }
    }
}
