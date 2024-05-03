package benchmark.generators.tools.runners.gt.hacks.interfile;

import com.github.gumtreediff.matchers.MappingStore;

import java.util.HashMap;
import java.util.Map;

/* Created by pourya on 2024-04-28*/
public class EfficientProjectGumTreeASTDiffProvider extends AbstractProjectGumTreeASTDiffProvider
{
    static Map<String, MappingStore> cache = new HashMap<>();
    private final AbstractProjectGumTreeASTDiffProvider projectGumTreeASTDiff;

    public EfficientProjectGumTreeASTDiffProvider(AbstractProjectGumTreeASTDiffProvider projectGumTreeASTDiff) {
        super(projectGumTreeASTDiff);
        this.projectGumTreeASTDiff = projectGumTreeASTDiff;
    }

    private String getKey(){
        return projectGumTreeASTDiff.getClass() + "*" + info.makeURL() + "*" + conf;
    }
    @Override
    public MappingStore getCommitLevelFullMatch() {
        String key = getKey();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        MappingStore commitLevelFullMatch = projectGumTreeASTDiff.getCommitLevelFullMatch();
        cache.put(key, commitLevelFullMatch);
        return commitLevelFullMatch;
    }
}
