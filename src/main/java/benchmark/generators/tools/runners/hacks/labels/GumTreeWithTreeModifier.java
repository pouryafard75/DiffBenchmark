package benchmark.generators.tools.runners.hacks.labels;

import benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.LinkedHashMap;
import java.util.Map;

/* Created by pourya on 2024-05-01*/
public class GumTreeWithTreeModifier extends BaseGumTreeASTDiffProvider{
    final TreeModifier treeModifier;
    public GumTreeWithTreeModifier(TreeModifier treeModifier, CompositeMatchers.CompositeMatcher matcher, ASTDiff input) {
        super(matcher, input);
        this.treeModifier = treeModifier;
    }

    @Override
    public ASTDiff makeASTDiff() throws Exception {
        Map<Tree, Tree> srcMap = new LinkedHashMap<>();
        Map<Tree, Tree> dstMap = new LinkedHashMap<>();
        Tree srcCopy = TreeUtilFunctions.deepCopyWithMap(input.src.getRoot(), srcMap);
        Tree dstCopy = TreeUtilFunctions.deepCopyWithMap(input.dst.getRoot(), dstMap);
        treeModifier.modify(srcCopy); treeModifier.modify(dstCopy);
        MappingStore copyMatch = match(srcCopy, dstCopy, matcher);
        MappingStore realMatch = new MappingStore(input.src.getRoot(), input.dst.getRoot());
        for (Mapping mapping : copyMatch) {
            Tree realSrc = srcMap.get(mapping.first);
            Tree realDst = dstMap.get(mapping.second);
            if (realSrc != null && realDst != null)
                if (realSrc.getType().name.equals(realDst.getType().name))
                    realMatch.addMapping(realSrc,realDst);
        }
        return mappingStoreToASTDiffWithActions(input.src.getRoot(), input.dst.getRoot(), realMatch);
    }
}
