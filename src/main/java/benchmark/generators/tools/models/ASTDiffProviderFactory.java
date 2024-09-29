package benchmark.generators.tools.models;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.IExperiment;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

public interface ASTDiffProviderFactory {

    /**
     * @param projectASTDiff contains all the src and dst trees + contents
     * @param input          contains the src and dst tree for the file that we want to diff
     * @param info           contains the meta information on the case in the benchmark, such as commit url and ...
     * @param experiment     contains the information on the configuration of the benchmark (in case you have different configurations)
     * @return the ASTDiffer returns a class that can be used to get the diff between the src and dst trees.
     */
    ASTDiffProvider getASTDiffer(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info, IExperiment experiment) throws Exception;
}
