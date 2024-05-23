package benchmark.generators.tools.models;

import org.refactoringminer.astDiff.models.ASTDiff;

/* Created by pourya on 2024-05-02*/

/**
 * Base class for ASTDiffProvider implementations.
 * This one works with one ASTDiff object as input,
 * Relies on the trees available in the ASTDiff object.
 */
public abstract class BaseASTDiffProvider implements ASTDiffProvider {
    protected final ASTDiff input;
    protected BaseASTDiffProvider(ASTDiff rmAstDiff) {
        input = rmAstDiff;
    }
}
