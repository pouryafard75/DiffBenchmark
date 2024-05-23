package benchmark.generators.tools.runners.hacks.interfile;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;

public interface GumTreeProjectMatcher {
    /**
     * In case you want to match the full tree, you can use this method
     */
    Iterable<Mapping> getCommitLevelFullMatch(Tree srcPT, Tree dstPT, Matcher matcher);


//    /**
//     * In case you have the mappings, and you want to use this interface as an additional round to generate inter-file mappings
//     */
//    ExtendedMultiMappingStore getCommitLevelFullMatch(Tree srcPT, Tree dstPT, Matcher matcher, ExtendedMultiMappingStore extendedMultiMappingStore);




}
