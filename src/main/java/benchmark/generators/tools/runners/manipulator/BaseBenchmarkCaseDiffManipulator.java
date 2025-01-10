package benchmark.generators.tools.runners.manipulator;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProvider;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.models.selector.DiffSelector;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.LinkedHashMap;
import java.util.Map;
/* Created by pourya on 2025-01-07*/

public class BaseBenchmarkCaseDiffManipulator implements IASTDiffTool {
    private final Map<DiffSelector, ASTDiff> memorized = new LinkedHashMap<>();
    protected final IBenchmarkCase benchmarkCase;

    public BaseBenchmarkCaseDiffManipulator(IBenchmarkCase benchmarkCase) {
        this.benchmarkCase = benchmarkCase;
    }

    @Override
    public String getToolName() {
        return "Manipulator";
    }
    @Override
    public String getShortName() {
        return "MAN";
    }
    @Override
    public ASTDiffProvider apply(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
        for (Map.Entry<DiffSelector, ASTDiff> iQuerySelectorASTDiffEntry : memorized.entrySet()) {
            ASTDiff selectorFromMemory = iQuerySelectorASTDiffEntry.getKey().apply(benchmarkCase.getProjectASTDiff());
            ASTDiff selected = querySelector.apply(benchmarkCase.getProjectASTDiff());
            if (areASTDiffsEqual(selectorFromMemory, selected))
                return iQuerySelectorASTDiffEntry::getValue;
        }
        throw new RuntimeException("Not found");
    }

    private static boolean areASTDiffsEqual(ASTDiff p, ASTDiff q) {
        if (p == null || q == null)
            return false;
        return p.getSrcPath().equals(q.getSrcPath()) && p.getDstPath().equals(q.getDstPath());
    }

    public void memorize(DiffSelector selector, ASTDiff astDiff) {
        memorized.put(selector, astDiff);
    }
};
