package diffQ;

import org.refactoringminer.astDiff.models.ASTDiff;

public interface DiffSimilarityCalculator {
    /**
     * Calculate the similarity between two diffs.
     *
     * @param diff1 The first ASTDiff.
     * @param diff2 The second ASTDiff.
     * @return A similarity score, higher means more similar.
     */
    double calculateSimilarity(ASTDiff diff1, ASTDiff diff2);
}
