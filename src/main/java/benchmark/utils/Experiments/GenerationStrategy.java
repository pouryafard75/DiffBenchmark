package benchmark.utils.Experiments;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.hrd.HRDGen2;
import benchmark.generators.hrd.HRDGen3;
import benchmark.generators.hrd.HumanReadableDiffGenerator;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

public enum GenerationStrategy implements IGenerationStrategy {
    NonFiltered {
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff,
                                               ASTDiff generated,
                                               BenchmarkCase info) {
            return new HRDGen3(projectASTDiff, generated, info);
        }
    },
    Filtered {
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, BenchmarkCase info) {
            return new HRDGen2(projectASTDiff, generated, info);
        }
    },

    Experimental{
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, BenchmarkCase info) {
            return new HRDGen3(projectASTDiff, generated, info);
        }
    };

    public abstract HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff,
                                                            ASTDiff generated,
                                                            BenchmarkCase info);

}
