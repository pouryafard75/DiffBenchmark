package benchmark.generators.tools.runners.hacks.labels;

import com.github.gumtreediff.tree.Tree;

import java.util.function.BiConsumer;
import java.util.function.Function;

/* Created by pourya on 2024-05-02*/
public class LeafLabelMerger extends AbstractLeafMergerTreeModifier {
    private static final Function<Tree, String> getLabel = Tree::getLabel;
    private static final BiConsumer<Tree, String> setLabel = Tree::setLabel;
    public LeafLabelMerger() {
        super(getLabel, setLabel);
    }
}
