package benchmark.generators.tools.runners.hacks.interfile;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.FakeTree;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

/* Created by pourya on 2024-04-30*/
public class StagedTreeMatching implements GumTreeProjectMatcher {

    @Override
    public MappingStore getCommitLevelFullMatch(Tree srcPT, Tree dstPT, Matcher matcher) {
        MappingStore match = new MappingStore(srcPT, dstPT);
        int sideWithMinSize = Math.min(srcPT.getChildren().size(), dstPT.getChildren().size());
        for (int i = 0; i < sideWithMinSize; i++) {
            Tree srcChild = srcPT.getChild(i);
            Tree dstChild = dstPT.getChild(i);

            Tree srcParent = srcChild.getParent(); Tree dstParent = dstChild.getParent();
            srcChild.setParent(null); dstChild.setParent(null);

            //Iterative Matcher
            match = matcher.match(srcChild, dstChild, match);
            match.addMapping(srcChild, dstChild); //For the compilation units
            srcChild.setParent(srcParent); dstChild.setParent(dstParent);
        }
        Tree srcPTRoot = srcPT.getParent(); Tree dstPTRoot = dstPT.getParent();
        srcPT.setParent(null); dstPT.setParent(null);
        //Full Matcher
        MappingStore result = matcher.match(srcPT, dstPT, match);
        srcPT.setParent(srcPTRoot); dstPT.setParent(dstPTRoot);
        if (srcPT instanceof FakeTree && dstPT instanceof FakeTree){
            result.removeMapping(srcPT, dstPT);
        }
        return result;
    }
}
