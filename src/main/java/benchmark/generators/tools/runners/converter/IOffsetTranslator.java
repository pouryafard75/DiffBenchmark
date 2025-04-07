package benchmark.generators.tools.runners.converter;

import org.refactoringminer.astDiff.models.ASTDiff;

public interface IOffsetTranslator {
    ASTDiff translate(ASTDiff foreign);
}
