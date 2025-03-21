package benchmark.generators.tools.runners.converter;

import benchmark.generators.tools.models.ASTDiffProvider;
import org.refactoringminer.astDiff.models.ASTDiff;

public class NoRulesOffsetTranslator implements ASTDiffProvider {

    protected final MappingOffsetTranslator mappingOffsetTranslator;
    protected final ASTDiffProvider provider;

    public NoRulesOffsetTranslator(ASTDiffProvider provider) {
        this.provider = provider;
        try {
            this.mappingOffsetTranslator = new MappingOffsetTranslator(provider.getASTDiff());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
        ASTDiff translated = getTranslatedASTDiffWithNoActions();
        translated.computeVanillaEditScript();
        return translated;
    }

    protected ASTDiff getTranslatedASTDiffWithNoActions() throws Exception {
        return mappingOffsetTranslator.translate(provider.getASTDiff());
    }
}
