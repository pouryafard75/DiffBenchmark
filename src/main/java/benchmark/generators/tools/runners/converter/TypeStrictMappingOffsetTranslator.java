package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.function.BiPredicate;

/* Created by pourya on 2025-03-21*/
public class TypeStrictMappingOffsetTranslator extends MappingOffsetTranslator {
    private static final BiPredicate<Tree, Tree>[] biPredicates = new BiPredicate[]{startOffsetMatchPredicate, endOffsetMatchPredicate, typeMatchPredicate};
    public TypeStrictMappingOffsetTranslator(ASTDiff ref, ITranslationRuleProvider ruleProvider) {
        super(ref, ruleProvider, biPredicates);
    }

}
