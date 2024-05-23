package benchmark.generators.tools.models;

import org.refactoringminer.astDiff.models.ASTDiff;

public interface ASTDiffProvider {
    ASTDiff makeASTDiff() throws Exception;
}
