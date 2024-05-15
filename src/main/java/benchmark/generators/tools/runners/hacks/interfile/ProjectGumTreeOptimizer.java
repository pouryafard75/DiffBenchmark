package benchmark.generators.tools.runners.hacks.interfile;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;

import java.util.HashMap;
import java.util.Map;
//
///* Created by pourya on 2024-04-28*/
public class ProjectGumTreeOptimizer extends ProjectGumTreeASTDiffProvider
{
    public static Map<String, Iterable<Mapping>> cache = new HashMap<>();
    private final ProjectGumTreeASTDiffProvider projectGumTreeASTDiff;

    public ProjectGumTreeOptimizer(ProjectGumTreeASTDiffProvider projectGumTreeASTDiff) {
        super(projectGumTreeASTDiff);
        this.projectGumTreeASTDiff = projectGumTreeASTDiff;
    }

    private String getKey(){
        return projectGumTreeASTDiff.matcherID()
                + "@" + info.makeURL() +
                "@" + conf.getName();
    }
    @Override
    public Iterable<Mapping> getFullMatch(Tree srcPT, Tree dstPT) {
        String key = getKey();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Iterable<Mapping> commitLevelFullMatch = projectGumTreeASTDiff.getFullMatch(srcPT, dstPT);
        cache.put(key, commitLevelFullMatch);
        return commitLevelFullMatch;
    }
}
