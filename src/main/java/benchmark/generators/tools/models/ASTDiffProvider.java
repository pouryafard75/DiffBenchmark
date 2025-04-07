package benchmark.generators.tools.models;

import org.refactoringminer.astDiff.models.ASTDiff;

public interface ASTDiffProvider {
    /**
     * @return the ASTDiffer returns the ASTDiff containing all the mappings
     */
    ASTDiff getASTDiff() throws Exception;
}