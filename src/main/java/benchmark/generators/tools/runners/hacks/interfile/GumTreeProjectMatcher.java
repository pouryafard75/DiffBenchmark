package benchmark.generators.tools.runners.hacks.interfile;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;

public interface GumTreeProjectMatcher {
    Iterable<Mapping> getCommitLevelFullMatch(Tree srcPT, Tree dstPT, Matcher matcher);
}
