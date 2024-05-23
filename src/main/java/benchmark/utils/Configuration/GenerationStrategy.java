package benchmark.utils.Configuration;

import benchmark.generators.hrd.HRDGen2;
import benchmark.generators.hrd.HRDGen3;
import benchmark.generators.hrd.HumanReadableDiffGenerator;
import benchmark.utils.CaseInfo;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

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
    },

    Experimental{
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, CaseInfo info, Configuration configuration) {
            return new HRDGen3(projectASTDiff, generated, info, configuration);
        }
    };

    public abstract HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff,
                                                            ASTDiff generated,
                                                            CaseInfo info,
                                                            Configuration configuration);

}
