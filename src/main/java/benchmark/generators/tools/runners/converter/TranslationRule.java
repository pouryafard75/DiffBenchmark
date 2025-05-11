package benchmark.generators.tools.runners.converter;


import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.apache.logging.log4j.util.TriConsumer;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

public interface TranslationRule extends TriConsumer<Mapping, Mapping, ExtendedMultiMappingStore> {
    void accept(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms);
    default void add(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms, Mapping m) {
        ms.addMapping(m.first, m.second);
        System.out.println("Adding mapping: " + m.first + " -> " + m.second + " for " + foreign + " -> " + local + " by " + this.getClass().getSimpleName());
    }
    default void addRecursively(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms, Tree t1, Tree t2) {
        if (t1 != null && t2 != null) {
            ms.addMappingRecursively(t1, t2);
            System.out.println("Adding mapping recursively: " + t1 + " -> " + t2 + " for " + foreign + " -> " + local + " by " + this.getClass().getSimpleName());
        }
    }
}

