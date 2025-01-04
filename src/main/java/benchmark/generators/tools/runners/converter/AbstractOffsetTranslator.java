package benchmark.generators.tools.runners.converter;

import org.refactoringminer.astDiff.models.ASTDiff;

/* Created by pourya on 2024-11-07*/
public abstract class AbstractOffsetTranslator implements IOffsetTranslator {

    protected final ASTDiff reference;
    public AbstractOffsetTranslator(ASTDiff ref) {
        this.reference = ref;
    }
}
