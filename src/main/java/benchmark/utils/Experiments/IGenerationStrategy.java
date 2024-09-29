package benchmark.utils.Experiments;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.ExperimentConfiguration;
import benchmark.data.exp.IExperiment;
import benchmark.generators.hrd.HumanReadableDiffGenerator;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

public interface IGenerationStrategy {
    HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, BenchmarkCase info);
}
