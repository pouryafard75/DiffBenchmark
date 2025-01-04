package benchmark.generators.tools.runners.gt;

import benchmark.generators.tools.models.BaseASTDiffProvider;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-04-30*/

// GumTree is an algorithm to match trees, (it's not an ASTDiff tool!, even if they claim so), in order to make use of with, you need a visitor, though
// This class basically relies on their own original visitor which might be even buggy in some cases (such as records and ...)
// the srcRoot and dstRoot, are the roots that we want to process, depends on the implemention, it's either coming from input's already generated tree
// or we can generate it from the input's source code, so the distinction is very important!

public class BaseGumTreeASTDiffProvider extends BaseASTDiffProvider {

    protected final Matcher matcher;
    private Tree srcRoot;
    private Tree dstRoot;

    protected BaseGumTreeASTDiffProvider(Matcher matcher, ASTDiff input) {
        super(input);
        this.matcher = matcher;
        setSrcRoot(input.src.getRoot());
        setDstRoot(input.dst.getRoot());
    }

    protected void setDstRoot(Tree inputTreeRoot) {
        dstRoot = inputTreeRoot;
    }

    protected void setSrcRoot(Tree inputTreeRoot) {
        srcRoot = inputTreeRoot;
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
        return makeASTDiffFromMatcher();
    }

    private ASTDiff makeASTDiffFromMatcher() {
        MappingStore match = match(srcRoot, dstRoot, matcher);
        return mappingStoreToASTDiffWithActions(srcRoot, dstRoot, match);
    }

    protected ASTDiff mappingStoreToASTDiffWithActions(Tree srcRoot, Tree dstRoot, MappingStore match) {
        Tree srcParent = srcRoot.getParent();
        Tree dstParent = dstRoot.getParent();
        ExtendedMultiMappingStore mappingStore = new ExtendedMultiMappingStore(srcRoot, dstRoot);
        mappingStore.add(match);
        EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
        TreeContext srcContext = new TreeContext();
        srcContext.setRoot(srcRoot);
        TreeContext dstContext = new TreeContext();
        dstContext.setRoot(dstRoot);
        ASTDiff diff = new ASTDiff(input.getSrcPath(), input.getDstPath(), srcContext, dstContext, mappingStore, actions);
        if (diff.getAllMappings().size() != match.size())
            if (!input.getSrcPath().equals("src_java_org_apache_commons_lang_math_NumberUtils.java"))
                throw new RuntimeException("Mapping has been lost!");
        srcRoot.setParent(srcParent);
        dstRoot.setParent(dstParent);
        return diff;
    }

    public static MappingStore match(Tree srcRoot, Tree dstRoot, Matcher matcher){
        return matcher.match(srcRoot, dstRoot);
    }
    public static void safeAdd(ExtendedMultiMappingStore extendedMultiMappingStore, Iterable<Mapping> match) {
        for (Mapping mapping : match) {
            safeAdd(extendedMultiMappingStore, mapping);
        }
    }

    protected static void safeAdd(ExtendedMultiMappingStore extendedMultiMappingStore, Mapping mapping) {
        if (!mapping.first.getType().name.equals(mapping.second.getType().name)) return;
        extendedMultiMappingStore.addMapping(mapping.first, mapping.second);
    }
}
