package benchmark.generators.tools.models;

import iast.com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

public interface ASTDiffProvider {
    /**
     * @return the ASTDiffer returns the ASTDiff containing all the mappings
     */
    ASTDiff makeASTDiff() throws Exception;
}