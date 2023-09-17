package benchmark.utils;

import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

public interface DiffToolFactory {
    ASTDiff getASTDiff(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo info, Configuration configuration);
}
