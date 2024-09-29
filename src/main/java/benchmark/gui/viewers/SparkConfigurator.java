package benchmark.gui.viewers;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.generators.tools.models.IASTDiffTool;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.Set;

public interface SparkConfigurator {
    void configure(IASTDiffTool tool, Set<ASTDiff> astDiffs, ProjectASTDiff projectASTDiff) throws Exception;
}
