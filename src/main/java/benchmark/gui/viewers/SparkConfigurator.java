package benchmark.gui.viewers;

import benchmark.generators.tools.ASTDiffTool;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.Set;

public interface SparkConfigurator {
    void configure(ASTDiffTool tool, Set<ASTDiff> astDiffs, ProjectASTDiff projectASTDiff) throws Exception;
}
