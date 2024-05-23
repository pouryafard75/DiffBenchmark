package benchmark.generators.tools.runners.hacks.multimapping;

import benchmark.generators.tools.runners.gt.BaseGumTreeASTDiffProvider;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.matchers.*;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.actions.editscript.SimplifiedExtendedChawatheScriptGenerator;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-05-07*/
public class GumTreeWithMultiMappingASTDiffProvider extends BaseGumTreeASTDiffProvider {
    final GumTreeMultiMappingMatcher gumTreeMultiMappingMatcher;
    public GumTreeWithMultiMappingASTDiffProvider(GumTreeMultiMappingMatcher gumTreeMultiMappingMatcher, CompositeMatchers.CompositeMatcher matcher, ASTDiff rmAstDiff) {
        super(matcher, rmAstDiff);
        this.gumTreeMultiMappingMatcher = gumTreeMultiMappingMatcher;
    }

    @Override
    public ASTDiff makeASTDiff() throws Exception {
        Tree srcRoot = input.src.getRoot();
        Tree dstRoot = input.dst.getRoot();
        MappingStore originalGumTreeMappingStore = match(srcRoot, dstRoot, matcher);
        ExtendedMultiMappingStore extendedMultiMappingStore = gumTreeMultiMappingMatcher.multimatch(srcRoot, dstRoot, matcher, originalGumTreeMappingStore);
        EditScript actions = new SimplifiedExtendedChawatheScriptGenerator().computeActions(extendedMultiMappingStore, null, null);
        return new ASTDiff(input.getSrcPath(), input.getDstPath(), input.src, input.dst, extendedMultiMappingStore, actions);
    }
}
