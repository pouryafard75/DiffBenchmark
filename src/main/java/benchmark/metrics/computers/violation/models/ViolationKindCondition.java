package benchmark.metrics.computers.violation.models;

import benchmark.oracle.generators.tools.models.ASTDiffTool;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.actions.ASTDiff;

/* Created by pourya on 2023-12-13 10:41 p.m. */
@FunctionalInterface
public interface ViolationKindCondition
        extends TriPredicate
        <Mapping, ASTDiffTool, ASTDiff> {
}
