package benchmark.metrics.computers.violation.models;

import benchmark.generators.tools.ASTDiffTool;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;

/* Created by pourya on 2023-12-13 10:41 p.m. */
@FunctionalInterface
public interface ViolationKindCondition
        extends TriPredicate
        <Mapping, ASTDiffTool, ASTDiff> {
}
