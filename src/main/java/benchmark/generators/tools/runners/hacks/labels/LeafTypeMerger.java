package benchmark.generators.tools.runners.hacks.labels;

import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TypeSet;

import java.util.function.BiConsumer;
import java.util.function.Function;

/* Created by pourya on 2024-05-02*/
public class LeafTypeMerger extends AbstractLeafMergerTreeModifier {
    private static final Function<Tree, String> getType = (tree) -> tree.getType().name;
    private static final BiConsumer<Tree, String> setType = (tree, input) -> tree.setType(TypeSet.type(input));
    public LeafTypeMerger() {
        super(getType, setType);
    }
}
