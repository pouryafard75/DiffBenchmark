package benchmark.generators.tools.runners.converter;


import com.github.gumtreediff.matchers.Mapping;
import org.apache.logging.log4j.util.TriConsumer;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

public interface TranslationRule extends TriConsumer<Mapping, Mapping, ExtendedMultiMappingStore> {
    void accept(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms);
}

