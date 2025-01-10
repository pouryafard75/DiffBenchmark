package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.models.selector.DiffSelector;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.List;

/* Created by pourya on 2024-10-07*/
public class SpoonWithOffsetTranslation extends Spoon implements ITranslationRuleProvider {

    protected MappingOffsetTranslator mappingOffsetTranslator;


    public SpoonWithOffsetTranslation(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
        super(benchmarkCase, querySelector);
        mappingOffsetTranslator = new MappingOffsetTranslator(input, this);
    }

    public SpoonWithOffsetTranslation(IBenchmarkCase benchmarkCase, DiffSelector querySelector, boolean noRules) {
        super(benchmarkCase, querySelector);
        mappingOffsetTranslator = new MappingOffsetTranslator(input);
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
        ASTDiff translated = getTranslatedASTDiffWithNoActions();
        translated.computeVanillaEditScript();
        return translated;
    }

    protected ASTDiff getTranslatedASTDiffWithNoActions() throws Exception {
        return mappingOffsetTranslator.translate(super.getASTDiff());
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


