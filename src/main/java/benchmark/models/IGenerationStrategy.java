package benchmark.models;

import benchmark.models.hrd.HumanReadableDiff;
import org.refactoringminer.astDiff.models.ASTDiff;

public interface IGenerationStrategy extends IGenerator<ASTDiff, HumanReadableDiff> {}



