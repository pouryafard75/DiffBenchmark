package benchmark.generators.tools.runners.trivial;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import java.util.Set;
import java.util.function.Predicate;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.isProgramElement;

public class TrivialDiff extends BaseTrivialDiff {

    private ExtendedMultiMappingStore perfectMappings = null;

    public TrivialDiff(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
        super(querySelector.apply(benchmarkCase.getProjectASTDiff()));
        try {
            perfectMappings = ASTDiffToolEnum.GOD.diff(benchmarkCase, querySelector).getAllMappings();
        } catch (Exception e) {
//            e.printStackTrace();
        }
        if (perfectMappings == null) {
            System.out.println("Perfect mappings are not available. Trivial diff will be performed without perfect mappings.");
            this.setCondition(mapping -> true);
        }
        Predicate<Mapping> predicate = mapping -> {
            if (!IdenticalProgramElementsDiffBaseTrivialDiff.mappingPredicate.test(mapping)) return false;
            if (perfectMappings == null) return true;
            Set<Tree> dsts = perfectMappings.getDsts(mapping.first);
            if (dsts == null) return true;
            return (dsts.contains(mapping.second));
        };
        this.setCondition(predicate);
    }
    private static class IdenticalProgramElementsDiffBaseTrivialDiff extends BaseTrivialDiff {
        public static final Predicate<Mapping> mappingPredicate = mapping -> isProgramElement(mapping.first.getType().name);
        public IdenticalProgramElementsDiffBaseTrivialDiff(ASTDiff rm_astDiff){
            super(rm_astDiff, mappingPredicate);
        }
    }
}
