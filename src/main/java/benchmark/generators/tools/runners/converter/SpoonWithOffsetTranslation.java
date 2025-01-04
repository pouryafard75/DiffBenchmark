package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.utils.Experiments.IQuerySelector;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.List;

/* Created by pourya on 2024-10-07*/
public class SpoonWithOffsetTranslation extends Spoon implements ITranslationRuleProvider {

    private final MappingOffsetTranslator mappingOffsetTranslator;


    public SpoonWithOffsetTranslation(IBenchmarkCase benchmarkCase, IQuerySelector querySelector) {
        super(benchmarkCase, querySelector);
        mappingOffsetTranslator = new MappingOffsetTranslator(input, this);
    }

    public SpoonWithOffsetTranslation(IBenchmarkCase benchmarkCase, IQuerySelector querySelector, boolean noRules) {
        super(benchmarkCase, querySelector);
        mappingOffsetTranslator = new MappingOffsetTranslator(input);
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
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

                //TODO: actually a very dissappinting one
                //do more favors including the imports
                //fix the offsets of the mappings in case of having comments

        );
    }
}


