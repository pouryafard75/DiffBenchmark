package benchmark.metrics.computers.violation;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.metrics.computers.violation.models.SemanticViolationRecord;
import benchmark.metrics.computers.violation.models.ViolationKind;
import benchmark.metrics.computers.violation.models.ViolationReport;
import benchmark.generators.tools.ASTDiffToolEnum;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static benchmark.metrics.computers.violation.Helpers.contains;
import static benchmark.metrics.computers.violation.Helpers.makeKey;
import static benchmark.generators.tools.ASTDiffToolEnum.GOD;

/* Created by pourya on 2023-12-10 9:02 p.m. */
public class BenchmarkViolationComputer {
    private final static Logger logger = LoggerFactory.getLogger(BenchmarkViolationComputer.class);
    private final Collection<ViolationReport> reports;
    private final IExperiment[] experiments;

    public IExperiment[] getExperiments() {
        return experiments;
    }

    public Collection<ViolationReport> getReports() {
        return reports;
    }

    private static boolean areAllConfigurationHavingTheSameTools(IExperiment[] experiments) {
        if (experiments.length < 1) {
            // If there is only one or zero configurations, they are considered identical
            throw new RuntimeException("There should be at least one configuration");
        }
        // Compare each configuration with the first configuration
        Set<IASTDiffTool> referenceArray = experiments[0].getTools();
        for (int i = 1; i < experiments.length; i++) {
            Set<IASTDiffTool> currentArray = experiments[i].getTools();
            if (!Arrays.equals(referenceArray.toArray(), currentArray.toArray())) {
                return false; // Arrays are not identical
            }
        }
        return true; // All configurations have the same ASTDiffTools array
    }

    /**
     * This constructor is used when only some violation kinds must be considered
     * @param experiments This must be used to make sure all the configurations are working with the same set of tools
     * @param violationKindsToCheck This is the set of violation kinds that must be considered
     */
    public BenchmarkViolationComputer(IExperiment[] experiments, ViolationKind[] violationKindsToCheck) {
        if (!areAllConfigurationHavingTheSameTools(experiments))
            throw new RuntimeException("Configuration tools are not identical");
        this.experiments = experiments;
        reports = ViolationReport.getWithArray(experiments[0], violationKindsToCheck);
    }
    /**
     * This constructor is used when all violation kinds must be considered
     * @param experiments This must be used to make sure all the configurations are working with the same set of tools
     */
    public BenchmarkViolationComputer(IExperiment[] experiments) {
        this(experiments, ViolationKind.values());
    }

    private void populateAllReports(ASTDiff perfect, ASTDiff generated, IASTDiffTool tool, IBenchmarkCase info, Collection<ViolationReport> reports) {
        for (Mapping mapping : generated.getAllMappings()) {
            for (ViolationReport report : reports) {
                if (report.getViolationKind().getCondition().test(mapping, tool, perfect))
                    if (!contains(perfect,mapping))
                        report.getViolations().get(tool).add(getSemanticViolationRecord(mapping, info.getID(), perfect.getSrcPath()));
            }
        }
    }

    public static SemanticViolationRecord getSemanticViolationRecord(Mapping violation, String infoURL, String filename) {
        return new SemanticViolationRecord(
                makeKey(violation), violation.first.toString(), violation.second.toString(), infoURL, filename);
    }

    public void compute(IBenchmarkCase info, IExperiment experiment) throws Exception {
        ProjectASTDiff projectASTDiff = info.getProjectASTDiff();
        for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet())
        {
            ASTDiff perfect = GOD.diff(info, (x -> rm_astDiff));
            for (IASTDiffTool tool : experiment.getTools())
            {
                if (tool.equals(ASTDiffToolEnum.GOD) || tool.equals(ASTDiffToolEnum.TRV)) continue; // GOD and TRV are not considered
//                logger.info("Generating " + tool.name());
                ASTDiff generated = tool.get(info, (x -> rm_astDiff)).getASTDiff();
//                logger.info("Comparing " + tool.name());
                populateAllReports(perfect, generated, tool, info, reports);
            }
        }
    }
}
