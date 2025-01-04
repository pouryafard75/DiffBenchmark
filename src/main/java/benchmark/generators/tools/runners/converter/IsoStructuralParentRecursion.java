package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-11-29*/
public class IsoStructuralParentRecursion implements TranslationRule {
    @Override
    public void accept(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms) {
        if (local.first.isIsoStructuralTo(local.second)) {
            ms.addMappingRecursively(local.first, local.second);
        }
    }
}
