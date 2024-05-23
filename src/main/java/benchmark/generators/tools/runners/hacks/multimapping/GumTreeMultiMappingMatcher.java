package benchmark.generators.tools.runners.hacks.multimapping;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

public interface GumTreeMultiMappingMatcher {
    ExtendedMultiMappingStore multimatch(Tree src, Tree dst, Matcher matcher, MappingStore mappings);

//    This method just returns the multi-mappings found by the implementation
//    Collection<Mapping> findMultiMappings(Tree src, Tree dst, Matcher matcher, MappingStore mappings);

}
