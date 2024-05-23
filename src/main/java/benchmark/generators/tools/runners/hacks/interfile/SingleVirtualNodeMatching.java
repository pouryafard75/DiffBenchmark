package benchmark.generators.tools.runners.hacks.interfile;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.FakeTree;
import com.github.gumtreediff.tree.Tree;

/* Created by pourya on 2024-05-12*/
public class SingleVirtualNodeMatching implements GumTreeProjectMatcher {
    @Override
    public Iterable<Mapping> getCommitLevelFullMatch(Tree srcPT, Tree dstPT, Matcher matcher) {
        Tree srcParent = srcPT.getParent(); Tree dstParent = dstPT.getParent();
        srcPT.setParent(null); dstPT.setParent(null);
        MappingStore result = matcher.match(srcPT, dstPT);
        srcPT.setParent(srcParent); dstPT.setParent(dstParent);
        if (srcPT instanceof FakeTree && dstPT instanceof FakeTree){
            result.removeMapping(srcPT, dstPT);
        }
        return result;
    }
}
