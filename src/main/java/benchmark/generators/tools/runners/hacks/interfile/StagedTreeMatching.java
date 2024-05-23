package benchmark.generators.tools.runners.hacks.interfile;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.FakeTree;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.function.BiConsumer;

/* Created by pourya on 2024-04-30*/
public class StagedTreeMatching implements GumTreeProjectMatcher {

    private final int numOfFiles;

    public StagedTreeMatching(ProjectASTDiff projectASTDiff) {
        numOfFiles = projectASTDiff.getDiffSet().size();
    }

    public int getNumOfFiles() {
        return numOfFiles;
    }

    @Override
    public Iterable<Mapping> getCommitLevelFullMatch(Tree srcPT, Tree dstPT, Matcher matcher) {
        MappingStore match = new MappingStore(srcPT, dstPT);
        iterativeRound(srcPT, dstPT,
                (srcChild, dstChild) -> {
                    matcher.match(srcChild, dstChild, match);
                    match.addMapping(srcChild, dstChild); //For the compilation units
        });
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

    public void iterativeRound(Tree srcPT, Tree dstPT, BiConsumer<Tree, Tree> function) {
        for (int i = 0; i < numOfFiles; i++) {
            Tree srcChild = srcPT.getChild(i);
            Tree dstChild = dstPT.getChild(i);
            Tree srcParent = srcChild.getParent(); Tree dstParent = dstChild.getParent();
            srcChild.setParent(null); dstChild.setParent(null);
            function.accept(srcChild, dstChild);
            srcChild.setParent(srcParent); dstChild.setParent(dstParent);
        }
    }

}

