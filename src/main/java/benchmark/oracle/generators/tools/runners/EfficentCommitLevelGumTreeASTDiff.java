package benchmark.oracle.generators.tools.runners;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.Matcher;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.util.HashMap;
import java.util.Map;

/* Created by pourya on 2024-04-28*/
public class EfficentCommitLevelGumTreeASTDiff extends CommitLevelGumTreeASTDiff {
    Map<String, MappingStore> cache = new HashMap<>();
    public EfficentCommitLevelGumTreeASTDiff(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo caseInfo, Configuration configuration, Matcher matcher) {
        super(projectASTDiff, rm_astDiff, caseInfo, configuration, matcher);
    }

    @Override
    public MappingStore getCommitLevelFullMatch() {
        String key = getKey();
        if (cache.containsKey(key)) {
            return cache.get(key);
        }
        MappingStore commitLevelFullMatch = super.getCommitLevelFullMatch();
        cache.put(key, commitLevelFullMatch);
        return commitLevelFullMatch;
    }
    private String getKey(){
        return info.makeURL() + "*" + configuration;
    }
}
