package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.diffcase.LocalCase;
import benchmark.data.diffcase.RemoteCase;
import benchmark.generators.tools.ASTDiffToolEnum;

import benchmark.utils.Experiments.IQuerySelector;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.io.File;
import java.util.Set;

import static benchmark.conf.Paths.ORACLE_DIR;

/* Created by pourya on 2024-02-19*/
public class RM2 extends AbstractASTDiffProviderFromMappingSet {
    public RM2(IBenchmarkCase benchmarkCase, IQuerySelector querySelector) {
        super(benchmarkCase, querySelector);
    }

    @Override
    protected Set<Mapping> getMappings() {
        Set<Mapping> mappings = null;
        for (rm2.refactoringminer.astDiff.actions.ASTDiff astDiff : runWhateverForRM2(benchmarkCase).getDiffSet()) {
            if (astDiff.getSrcPath().equals(input.getSrcPath())) {
                mappings = astDiff.getAllMappings().getMappings();
                break;
            }
        }
        return mappings;
    }

    @Override
    protected void postPopulation(ASTDiff astDiff) {
        ASTDiff trivialDiff = null;
        try {
            trivialDiff = ASTDiffToolEnum.TRV.diff(benchmarkCase, querySelector);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        for (Mapping m : trivialDiff.getAllMappings())  astDiff.getAllMappings().addMapping(m.first, m.second);
    }

    public static rm2.refactoringminer.astDiff.actions.ProjectASTDiff runWhateverForRM2(IBenchmarkCase caseInfo) {
        String repo = caseInfo.getRepo();
        String commit = caseInfo.getCommit();
        if (caseInfo instanceof LocalCase)
            return new rm2.refactoringminer.rm1.GitHistoryRefactoringMinerImpl().diffAtDirectories(((LocalCase) caseInfo).getSrcPath(), ((LocalCase) caseInfo).getDstPath());
        else if (caseInfo instanceof RemoteCase)
        {
            return new rm2.refactoringminer.rm1.GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(ORACLE_DIR));
        }
        else
            throw new IllegalArgumentException("Unknown case type");
    }
}
