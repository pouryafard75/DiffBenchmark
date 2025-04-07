package benchmark.models.selector;

import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

public interface DiffSelector {
    ASTDiff apply(ProjectASTDiff projectASTDiff);

    static DiffSelector any() {
        return projectASTDiff -> projectASTDiff.getDiffSet().iterator().next();
    }
}
