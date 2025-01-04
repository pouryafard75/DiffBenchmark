package benchmark.generators.tools.runners.trivial;

import benchmark.generators.tools.models.BaseASTDiffProvider;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

/* Created by pourya on 2024-10-04*/
public class EmptyDiff extends BaseASTDiffProvider {
    public EmptyDiff(ASTDiff rmAstDiff) {
        super(rmAstDiff);
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
        ExtendedMultiMappingStore extendedMultiMappingStore = new ExtendedMultiMappingStore(input.src.getRoot(), input.dst.getRoot());
        ASTDiff astDiff = new ASTDiff(input.getSrcPath(), input.getDstPath(), input.src, input.dst, extendedMultiMappingStore);
        astDiff.computeVanillaEditScript();
        return astDiff;
    }
}
