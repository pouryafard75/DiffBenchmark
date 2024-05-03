package benchmark.generators.tools.runners.gt.hacks.labels;

import benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeUtils;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.LinkedHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/* Created by pourya on 2024-05-01*/
public class BaseMergedNodesGumTreeASTDiffProvider extends BaseGumTreeASTDiffProvider {

    private final BiConsumer<Tree, String> setFunction;
    private final Function<Tree, String> getFunction;

    public BaseMergedNodesGumTreeASTDiffProvider(CompositeMatchers.CompositeMatcher matcher, ASTDiff input,  Function<Tree, String> getFunction , BiConsumer<Tree, String> setFunction) {
        super(matcher, input);
        this.setFunction = setFunction;
        this.getFunction = getFunction;
    }

    @Override
    public ASTDiff makeASTDiff() throws Exception {
        LinkedHashMap<Tree, Tree> srcMap = new LinkedHashMap<>();
        LinkedHashMap<Tree, Tree> dstMap = new LinkedHashMap<>();
        Tree srcCopy = TreeUtilFunctions.deepCopyWithMap(input.src.getRoot(), srcMap);
        Tree dstCopy = TreeUtilFunctions.deepCopyWithMap(input.dst.getRoot(), dstMap);
        merge(srcCopy); merge(dstCopy);
        MappingStore copyMatch = match(srcCopy, dstCopy);
        MappingStore realMatch = new MappingStore(input.src.getRoot(), input.dst.getRoot());
        for (Mapping mapping : copyMatch) {
            Tree realSrc = srcMap.get(mapping.first);
            Tree realDst = dstMap.get(mapping.second);
            if (realSrc != null && realDst != null)
                realMatch.addMapping(realSrc,realDst);
        }
        return mappingStoreToASTDiffWithActions(input.src.getRoot(), input.dst.getRoot(), realMatch);
    }

    protected void merge(Tree baseTree){
        mergeLeavesAndSingleChildInnersWithFunctions(baseTree, getFunction, setFunction);
    }
    protected void mergeLeavesAndSingleChildInnersWithFunctions(Tree baseTree, Function<Tree, String> getFunction, BiConsumer<Tree, String> setFunction) {
        for (Tree tree : TreeUtils.breadthFirst(baseTree)) {
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
