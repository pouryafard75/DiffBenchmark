package benchmark.generators.tools.runners.gt;

import benchmark.generators.tools.models.BaseASTDiffProvider;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

/* Created by pourya on 2024-04-30*/
public class BaseGumTreeASTDiffProvider extends BaseASTDiffProvider {

    protected final CompositeMatchers.CompositeMatcher matcher;
    protected BaseGumTreeASTDiffProvider(CompositeMatchers.CompositeMatcher matcher, ASTDiff input) {
        super(input);
        this.matcher = matcher;
    }

    @Override
    public ASTDiff makeASTDiff() throws Exception {
        return makeASTDiffFromMatcher();
    }

    private ASTDiff makeASTDiffFromMatcher() {
        ASTDiff astDiff = input;
        Tree srcRoot = astDiff.src.getRoot();
        Tree dstRoot = astDiff.dst.getRoot();
        MappingStore match = match(srcRoot, dstRoot);
        return mappingStoreToASTDiffWithActions(srcRoot, dstRoot, match);
    }

    protected ASTDiff mappingStoreToASTDiffWithActions(Tree srcRoot, Tree dstRoot, MappingStore match) {
        ASTDiff astDiff = input;
        Tree srcParent = srcRoot.getParent();
        Tree dstParent = dstRoot.getParent();
        ExtendedMultiMappingStore mappingStore = new ExtendedMultiMappingStore(srcRoot, dstRoot);
        mappingStore.add(match);
        EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
        ASTDiff diff = new ASTDiff(astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.src, astDiff.dst, mappingStore, actions);
        if (diff.getAllMappings().size() != match.size())
            if (!astDiff.getSrcPath().equals("src_java_org_apache_commons_lang_math_NumberUtils.java"))
                throw new RuntimeException("Mapping has been lost!");
        srcRoot.setParent(srcParent);
        dstRoot.setParent(dstParent);
        return diff;
    }

    public MappingStore match(Tree srcRoot, Tree dstRoot){
        Tree srcParent = srcRoot.getParent();
        Tree dstParent = dstRoot.getParent();
        srcRoot.setParent(null);
        dstRoot.setParent(null);
        MappingStore match = matcher.match(srcRoot, dstRoot);
        srcRoot.setParent(srcParent);
        dstRoot.setParent(dstParent);
        return match;
    }
}
