package benchmark.oracle.generators.tools.runners.trivial;

import benchmark.oracle.generators.tools.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

import java.util.Set;
import java.util.function.Predicate;

import static benchmark.oracle.generators.diff.HumanReadableDiffGenerator.isProgramElement;

public class TrivialDiff extends BaseTrivialDiff {

    private ExtendedMultiMappingStore perfectMappings = null;

    public TrivialDiff(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo info, Configuration conf) {
        super(rm_astDiff);
        try {
            perfectMappings = ASTDiffTool.GOD.diff(projectASTDiff, rm_astDiff, info, conf).getAllMappings();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Predicate<Mapping> predicate = mapping -> {
            if (!IdenticalProgramElementsDiffBaseTrivialDiff.mappingPredicate.test(mapping)) return false;
            if (perfectMappings == null) return true;
            Set<Tree> dsts = perfectMappings.getDsts(mapping.first);
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
