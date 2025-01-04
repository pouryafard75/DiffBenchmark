package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-11-29*/
public abstract class FirstChildOfTypeMatcher implements TranslationRule {
    final String query;
    public FirstChildOfTypeMatcher(String query) {
        this.query = query;
    }
    @Override
    public void accept(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms) {

        Tree first = local.first;
        Tree second = local.second;
        //get the child of first which has the type of query (if it exists)
        Tree firstQ = getFirstQ(first, query);
        //get the child of second which has the type of query (if it exists)
        Tree secondQ = getFirstQ(second, query);
        //if both firstQ and secondQ exist, add a mapping between them
        if (firstQ != null && secondQ != null) {
            ms.addMappingRecursively(firstQ, secondQ);
        }
    }

    private static Tree getFirstQ(Tree t, String query) {
        return t.getChildren().stream().filter(
                child -> child.getType().name.equals(query)).findFirst().orElse(null);
    }

}
