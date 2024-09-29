package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.diffcase.LocalCase;
import benchmark.data.diffcase.RemoteCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.runners.trivial.TrivialDiff;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.io.File;
import java.util.Set;

import static benchmark.conf.Paths.ORACLE_DIR;

/* Created by pourya on 2024-02-19*/
public class RM2 extends AbstractASTDiffProviderFromMappingSet {

    public RM2(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info) {
        super(projectASTDiff, input, info);
    }
    @Override
    protected Set<Mapping> getMappings() {
        Set<Mapping> mappings = null;
        for (rm2.refactoringminer.astDiff.actions.ASTDiff astDiff : runWhateverForRM2(info).getDiffSet()) {
            if (astDiff.getSrcPath().equals(input.getSrcPath())) {
                mappings = astDiff.getAllMappings().getMappings();
                break;
            }
        }
        return mappings;
    }

    @Override
    protected void postPopulation(ASTDiff astDiff) {
        ASTDiff trivialDiff = new TrivialDiff(projectASTDiff, input, info).makeASTDiff();
        for (Mapping m : trivialDiff.getAllMappings())  astDiff.getAllMappings().addMapping(m.first, m.second);
    }

    public static rm2.refactoringminer.astDiff.actions.ProjectASTDiff runWhateverForRM2(BenchmarkCase caseInfo) {
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
