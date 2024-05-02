package benchmark.oracle.generators.tools.models;

import org.refactoringminer.astDiff.actions.ASTDiff;

public interface ASTDiffProvider {
    ASTDiff makeASTDiff() throws Exception;
}
