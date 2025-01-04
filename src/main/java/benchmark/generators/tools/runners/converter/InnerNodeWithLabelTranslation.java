package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-11-29*/
public class InnerNodeWithLabelTranslation implements TranslationRule {
    @Override
    public void accept(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms) {
        if (innerNodeWithLabel(foreign)) {
            Tree t1 = translateInnerNodeWithLabel(foreign.first, local.first);
            Tree t2 = translateInnerNodeWithLabel(foreign.second, local.second);
            if (t1 != null && t2 != null)
                ms.addMapping(t1, t2);
        }
    }

    private static boolean innerNodeWithLabel(Mapping mapping) {
        return nonLeafWithLabel(mapping.first) || nonLeafWithLabel(mapping.second);
    }

    private static boolean nonLeafWithLabel(Tree entry) {
        return !entry.isLeaf() && !entry.getLabel().isEmpty();
    }

    private Tree translateInnerNodeWithLabel(Tree entry, Tree eqv) {
        if (eqv == null) return null;
        String label = entry.getLabel();
        if (label.isEmpty()) return null;
        for (Tree candidate : eqv.preOrder()) {
            if (candidate.getLabel().equals(label)) {
                return candidate;
            }
        }
//        System.out.println("Label not found: " + label);
        return null;
    }
}
