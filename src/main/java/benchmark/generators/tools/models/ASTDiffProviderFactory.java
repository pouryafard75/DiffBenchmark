package benchmark.generators.tools.models;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

public interface ASTDiffProviderFactory {
    ASTDiffProvider getASTDiffer(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo info, Configuration configuration) throws Exception;
}
