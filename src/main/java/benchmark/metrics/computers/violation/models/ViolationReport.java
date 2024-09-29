package benchmark.metrics.computers.violation.models;

import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.data.exp.ExperimentConfiguration;
import benchmark.generators.tools.models.IASTDiffTool;

import java.util.*;

/* Created by pourya on 2023-12-11 3:03 p.m. */
public class ViolationReport {
    private final ViolationKind violationKind;
    private final Map<IASTDiffTool, Collection<SemanticViolationRecord>> violations = new LinkedHashMap<>();
    ViolationReport(ViolationKind violationKind, IExperiment experiment) {
        this.violationKind = violationKind;
        for (IASTDiffTool tool : experiment.getTools()) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV)) continue; // GOD and TRV are not considered
            violations.put(tool, new LinkedHashSet<>());
        }
    }

    public ViolationKind getViolationKind() {
        return violationKind;
    }

    public Map<IASTDiffTool, Collection<SemanticViolationRecord>> getViolations() {
        return violations;
    }

    public static Collection<ViolationReport> getWithArray(IExperiment experiment, ViolationKind[] violationKindsToCheck) {
        if (violationKindsToCheck.length == 0) throw new RuntimeException("Violation kinds to check should not be empty");
        Collection<ViolationReport> violations = new LinkedHashSet<>();
        for (ViolationKind kind : ViolationKind.values()) {
            if (Arrays.asList(violationKindsToCheck).contains(kind)) {
                violations.add(new ViolationReport(kind, experiment));
            }
        }
        return violations;
    }
}
