package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.runners.trivial.BaseTrivialDiff;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import shadedspoon.gumtree.spoon.diff.DiffConfiguration;

import java.util.function.Predicate;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.*;

/* Created by pourya on 2025-01-09*/

// This is the Spoon + Add the mappings for identical Imports and identical Package Declarations
// And correcting offset mistakes in the presence of comments on top of the program elements
// Regarding the offset problems you can check https://github.com/SpoonLabs/gumtree-spoon-ast-diff/issues/336
public class FinalizedSpoon extends SpoonWithOffsetTranslation {

    public FinalizedSpoon(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
        super(benchmarkCase, querySelector);
        mappingOffsetTranslator = new CommentCorrectorMappingOffsetTranslator(input, this);
    }

    public FinalizedSpoon(IBenchmarkCase benchmarkCase, DiffSelector querySelector, DiffConfiguration configuration) {
        super(benchmarkCase, querySelector, configuration);
        mappingOffsetTranslator = new CommentCorrectorMappingOffsetTranslator(input, this);
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
        ASTDiff translated = getTranslatedASTDiffWithNoActions();
        IdenticalImportOrPkgMatcher identicalImportOrPkgMatcher = new IdenticalImportOrPkgMatcher(translated);
        for (Mapping m : identicalImportOrPkgMatcher.getASTDiff().getAllMappings()) {
            translated.getAllMappings().addMapping(m.first, m.second);
        }
        translated.computeVanillaEditScript();
        return translated;
    }

    private static class IdenticalImportOrPkgMatcher extends BaseTrivialDiff {
        public static final Predicate<Mapping> mappingPredicate =
                mapping -> isImport(mapping.first.getType().name) || isPackageDecl(mapping.first.getType().name);
        public IdenticalImportOrPkgMatcher(ASTDiff rm_astDiff){
            super(rm_astDiff, mappingPredicate);
        }
    }
}

