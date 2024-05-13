package benchmark.generators.tools.runners.hacks.interfile;

import com.github.gumtreediff.matchers.Mapping;

import java.util.HashMap;
import java.util.Map;
//
///* Created by pourya on 2024-04-28*/
public class ProjectGumTreeOptimizer extends ProjectGumTreeASTDiffProvider
{
    static Map<String, Iterable<Mapping>> cache = new HashMap<>();
    private final ProjectGumTreeASTDiffProvider projectGumTreeASTDiff;

    public ProjectGumTreeOptimizer(ProjectGumTreeASTDiffProvider projectGumTreeASTDiff) {
        super(projectGumTreeASTDiff);
        this.projectGumTreeASTDiff = projectGumTreeASTDiff;
    }

    private String getKey(){
        return projectGumTreeASTDiff.matcherID();
    }
    public Iterable<Mapping> getFullMatch() {
        String key = getKey();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        Iterable<Mapping> commitLevelFullMatch = projectGumTreeASTDiff.getFullMatch();
        cache.put(key, commitLevelFullMatch);
        return commitLevelFullMatch;
    }
}
