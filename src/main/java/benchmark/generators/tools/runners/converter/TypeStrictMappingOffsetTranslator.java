package benchmark.generators.tools.runners.converter;

import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.function.BiPredicate;

/* Created by pourya on 2025-03-21*/
public class TypeStrictMappingOffsetTranslator extends MappingOffsetTranslator {

@SuppressWarnings("unchecked")
public TypeStrictMappingOffsetTranslator(ASTDiff ref) {
        super(ref);
        predicates = new BiPredicate[]{
                    startOffsetMatchPredicate,
                    endOffsetMatchPredicate,
                    typeMatchPredicate
        };
    }

}
