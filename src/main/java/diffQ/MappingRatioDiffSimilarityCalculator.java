package diffQ;

import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

public class MappingRatioDiffSimilarityCalculator implements DiffSimilarityCalculator {
    @Override
    public double calculateSimilarity(ASTDiff diff1, ASTDiff diff2) {
        //This is supposed to count the number of mappings in both diffs and return the ratio (to the diff with more mappings)
        ExtendedMultiMappingStore m1 = diff1.getAllMappings();
        ExtendedMultiMappingStore m2 = diff2.getAllMappings();
        // Counting the mappings is not enough as we need to ensure the exact same mapping exists in both diffs
        double commonMappingsCount = 0;
        for (Mapping mapping : m1) if (m2.getMappings().contains(mapping)) commonMappingsCount ++;

        double denominator = Math.max(m1.getMappings().size(), m2.getMappings().size());
        if (denominator == 0) {
            return 0.0; // Avoid division by zero
        }
        double similarity = commonMappingsCount / denominator;
        // Normalize the similarity to be between 0 and 1
        return Math.min(Math.max(similarity, 0.0), 1.0);
    }
}
