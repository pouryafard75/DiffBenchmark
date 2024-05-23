package benchmark.metrics.computers.violation;

import benchmark.metrics.computers.violation.models.SemanticViolationRecord;
import benchmark.metrics.computers.violation.models.ViolationKind;
import benchmark.metrics.computers.violation.models.ViolationReport;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;

import static benchmark.metrics.computers.violation.Helpers.contains;
import static benchmark.metrics.computers.violation.Helpers.makeKey;
import static benchmark.generators.tools.ASTDiffTool.GOD;

/* Created by pourya on 2023-12-10 9:02 p.m. */
public class BenchmarkViolationComputer {
    private final static Logger logger = LoggerFactory.getLogger(BenchmarkViolationComputer.class);
    private final Collection<ViolationReport> reports;
    private final Configuration[] configurations;

    public Configuration[] getConfigurations() {
        return configurations;
    }

    public Collection<ViolationReport> getReports() {
        return reports;
    }

    private static boolean areAllConfigurationHavingTheSameTools(Configuration[] configurations) {
        if (configurations.length < 1) {
            // If there is only one or zero configurations, they are considered identical
            throw new RuntimeException("There should be at least one configuration");
        }
        // Compare each configuration with the first configuration
        ASTDiffTool[] referenceArray = configurations[0].getActiveTools();
        for (int i = 1; i < configurations.length; i++) {
            ASTDiffTool[] currentArray = configurations[i].getActiveTools();
            if (!Arrays.equals(referenceArray, currentArray)) {
                return false; // Arrays are not identical
            }
        }
        return true; // All configurations have the same ASTDiffTools array
    }

    /**
     * This constructor is used when only some violation kinds must be considered
     * @param configurations This must be used to make sure all the configurations are working with the same set of tools
     * @param violationKindsToCheck This is the set of violation kinds that must be considered
     */
    public BenchmarkViolationComputer(Configuration[] configurations, ViolationKind[] violationKindsToCheck) {
        if (!areAllConfigurationHavingTheSameTools(configurations))
            throw new RuntimeException("Configuration tools are not identical");
        this.configurations = configurations;
        reports = ViolationReport.getWithArray(configurations[0], violationKindsToCheck);
    }
    /**
     * This constructor is used when all violation kinds must be considered
     * @param configurations This must be used to make sure all the configurations are working with the same set of tools
     */
    public BenchmarkViolationComputer(Configuration[] configurations) {
        this(configurations, ViolationKind.values());
    }

    private void populateAllReports(ASTDiff perfect, ASTDiff generated, ASTDiffTool tool, CaseInfo info, Collection<ViolationReport> reports) {
        for (Mapping mapping : generated.getAllMappings()) {
            for (ViolationReport report : reports) {
                if (report.getViolationKind().getCondition().test(mapping, tool, perfect))
                    if (!contains(perfect,mapping))
                        report.getViolations().get(tool).add(getSemanticViolationRecord(mapping, info.makeURL(), perfect.getSrcPath()));
            }
        }
    }

    public static SemanticViolationRecord getSemanticViolationRecord(Mapping violation, String infoURL, String filename) {
        return new SemanticViolationRecord(
                makeKey(violation), violation.first.toString(), violation.second.toString(), infoURL, filename);
    }

    public void compute(ProjectASTDiff projectASTDiff, CaseInfo info, Configuration configuration) throws Exception {
        for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet())
        {
            ASTDiff perfect = GOD.diff(projectASTDiff, rm_astDiff, info, configuration);
            for (ASTDiffTool tool : configuration.getActiveTools())
            {
                if (tool.equals(ASTDiffTool.GOD) || tool.equals(ASTDiffTool.TRV)) continue; // GOD and TRV are not considered
//                logger.info("Generating " + tool.name());
                ASTDiff generated = tool.diff(projectASTDiff, rm_astDiff, info, configuration);
//                logger.info("Comparing " + tool.name());
                populateAllReports(perfect, generated, tool, info, reports);
            }
        }
    }
}
