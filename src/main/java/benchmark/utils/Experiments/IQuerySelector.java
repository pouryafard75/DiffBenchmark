package benchmark.utils.Experiments;

import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

public interface IQuerySelector {
    ASTDiff apply(ProjectASTDiff projectASTDiff);
}
