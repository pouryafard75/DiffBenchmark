package benchmark.metrics.computers.violation.models;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;

import java.util.*;

/* Created by pourya on 2023-12-11 3:03 p.m. */
public class ViolationReport {
    private final ViolationKind violationKind;
    private final Map<ASTDiffTool, Collection<SemanticViolationRecord>> violations = new LinkedHashMap<>();
    ViolationReport(ViolationKind violationKind, Configuration config) {
        this.violationKind = violationKind;
        for (ASTDiffTool tool : config.getActiveTools()) {
            if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV)) continue; // GOD and TRV are not considered
            violations.put(tool, new LinkedHashSet<>());
        }
    }

    public ViolationKind getViolationKind() {
        return violationKind;
    }

    public Map<ASTDiffTool, Collection<SemanticViolationRecord>> getViolations() {
        return violations;
    }

    public static Collection<ViolationReport> getWithArray(Configuration configuration, ViolationKind[] violationKindsToCheck) {
        if (violationKindsToCheck.length == 0) throw new RuntimeException("Violation kinds to check should not be empty");
        Collection<ViolationReport> violations = new LinkedHashSet<>();
        for (ViolationKind kind : ViolationKind.values()) {
            if (Arrays.asList(violationKindsToCheck).contains(kind)) {
                violations.add(new ViolationReport(kind, configuration));
            }
        }
        return violations;
    }
}
