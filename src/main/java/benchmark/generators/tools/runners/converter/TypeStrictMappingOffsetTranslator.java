package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.List;
import java.util.function.BiPredicate;

/* Created by pourya on 2025-03-21*/
public class TypeStrictMappingOffsetTranslator extends MappingOffsetTranslator {
    protected static final BiPredicate<Tree, Tree>[] biPredicates = new BiPredicate[]{startOffsetMatchPredicate, endOffsetMatchPredicate, typeMatchPredicate};
    public TypeStrictMappingOffsetTranslator(ASTDiff ref, ITranslationRuleProvider ruleProvider) {
        super(ref, ruleProvider, biPredicates);
    }

    @Override
    protected Tree investigate(Tree bad, Tree goodRoot) {
        List<Tree> candidates = getCandidates(bad, goodRoot, MappingOffsetTranslator.biPredicates);
        if (candidates.isEmpty()) return super.investigate(bad, goodRoot);
        return chooseBestCandidate(bad, candidates);
    }
}
