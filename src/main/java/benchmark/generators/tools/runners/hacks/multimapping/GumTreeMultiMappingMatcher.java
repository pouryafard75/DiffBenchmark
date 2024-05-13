package benchmark.generators.tools.runners.hacks.multimapping;

import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

public interface GumTreeMultiMappingMatcher {
    ExtendedMultiMappingStore multimatch(Tree src, Tree dst, Matcher matcher, MappingStore mappings);
}
