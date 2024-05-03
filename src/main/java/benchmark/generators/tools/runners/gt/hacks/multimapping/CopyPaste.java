package benchmark.generators.tools.runners.gt.hacks.multimapping;

import benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeUtils;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.SimplifiedChawatheScriptGenerator;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

import java.util.List;

/* Created by pourya on 2024-04-30*/
public class CopyPaste extends BaseGumTreeASTDiffProvider {

    public CopyPaste(CompositeMatchers.CompositeMatcher matcher, ASTDiff rmAstDiff) {
        super(matcher, rmAstDiff);
    }

    @Override
    public ASTDiff makeASTDiff() throws Exception {
        Tree srcRoot = input.src.getRoot();
        Tree dstRoot = input.dst.getRoot();
        ExtendedMultiMappingStore extendedMultiMappingStore = new ExtendedMultiMappingStore(srcRoot, dstRoot);
        extendedMultiMappingStore.add(match(srcRoot, dstRoot));
        addCopyPastes(srcRoot, dstRoot, extendedMultiMappingStore);
        EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(extendedMultiMappingStore, null, null);
        return new ASTDiff(input.getSrcPath(), input.getDstPath(), input.src, input.dst, extendedMultiMappingStore, actions);
    }

    private void addCopyPastes(Tree srcRoot, Tree dstRoot, ExtendedMultiMappingStore extendedMultiMappingStore) {
        List<Tree> srcTrees = TreeUtils.breadthFirst(srcRoot);
        for (Tree subTree : srcTrees) {
            if (!extendedMultiMappingStore.isSrcMapped(subTree)) {
                extendedMultiMappingStore.add(match(subTree, dstRoot));
            }
        }
        List<Tree> dstTrees = TreeUtils.breadthFirst(srcRoot);
        for (Tree subTree : dstTrees) {
            if (!extendedMultiMappingStore.isDstMapped(subTree)) {
                extendedMultiMappingStore.add(match(srcRoot, subTree));
            }
        }
    }
}
