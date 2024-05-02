package benchmark.oracle.generators.tools.models;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

public interface ASTDiffProviderFactory {
    ASTDiffProvider getASTDiffer(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo info, Configuration configuration) throws Exception;
}
