package benchmark.utils.Configuration;

import benchmark.oracle.generators.diff.HRDGen2;
import benchmark.oracle.generators.diff.HRDGen3;
import benchmark.oracle.generators.diff.HumanReadableDiffGenerator;
import benchmark.utils.CaseInfo;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

public enum GenerationStrategy {
    NonFiltered {
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff,
                                               ASTDiff generated,
                                               CaseInfo info,
                                               Configuration configuration) {
            return new HRDGen3(projectASTDiff, generated, info, configuration);
        }
    },
    Filtered {
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, CaseInfo info, Configuration configuration) {
            return new HRDGen2(projectASTDiff, generated, info, configuration);
        }
    };

    public abstract HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff,
                                                            ASTDiff generated,
                                                            CaseInfo info,
                                                            Configuration configuration);

}
