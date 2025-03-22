package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.function.BiPredicate;

/* Created by pourya on 2024-11-07*/
public abstract class AbstractOffsetTranslator implements IOffsetTranslator {
    protected static final BiPredicate<Tree, Tree> startOffsetMatchPredicate  = (candidate, badNode) -> candidate.getPos() == badNode.getPos();
    protected static final BiPredicate<Tree, Tree> endOffsetMatchPredicate  = (candidate, badNode) -> candidate.getEndPos() == badNode.getEndPos();
    protected static final BiPredicate<Tree, Tree> typeMatchPredicate  = (candidate, badNode) -> candidate.getType().name.equals(badNode.getType().name);

    protected final ASTDiff reference;
    public AbstractOffsetTranslator(ASTDiff ref) {
        this.reference = ref;
    }
}
