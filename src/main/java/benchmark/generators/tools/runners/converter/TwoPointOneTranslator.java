package benchmark.generators.tools.runners.converter;

import benchmark.generators.tools.models.ASTDiffProvider;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.List;

public class TwoPointOneTranslator implements ASTDiffProvider, ITranslationRuleProvider {

    protected final MappingOffsetTranslator mappingOffsetTranslator;
    protected final ASTDiffProvider provider;

    public TwoPointOneTranslator(ASTDiffProvider provider, ASTDiff ref) {
        this.provider = provider;
        try {
            this.mappingOffsetTranslator = new TypeStrictMappingOffsetTranslator(ref, this);
        } catch (Exception e) {
            System.err.println(e.getMessage());
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

    @Override
    public List<TranslationRule> getRules() {
        return List.of(
                new IsoStructuralParentRecursion(),
                new InnerNodeWithLabelTranslation(),
                new FirstChildOfTypeBlock(),
                new FirstChildOfTypeKind(),
                new ParentWithSameOffset()
        );
    }
}
