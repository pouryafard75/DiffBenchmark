package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.isBetweenDifferentTypes;

/* Created by pourya on 2024-11-29*/
public class ParentWithSameOffset implements TranslationRule {
    @Override
    public void accept(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms) {
        Tree p1 = local.first.getParent();
        Tree p2 = local.second.getParent();
        if (p1 != null && p2 != null) {
            if (p1.getPos() == local.first.getPos() && p1.getEndPos() == local.first.getEndPos() &&
                p2.getPos() == local.second.getPos() && p2.getEndPos() == local.second.getEndPos())
            {
                Mapping pMapping = new Mapping(p1, p2);
                if (!isBetweenDifferentTypes(pMapping)) {
                    ms.addMapping(pMapping.first, pMapping.second);
                    new ParentWithSameOffset().accept(null, pMapping, ms);
                }
            }
        }
    }
}
