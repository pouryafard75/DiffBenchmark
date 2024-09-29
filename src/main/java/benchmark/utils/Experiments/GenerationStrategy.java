package benchmark.utils.Experiments;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.ExperimentConfiguration;
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
                                               BenchmarkCase info,
                                               IExperiment experiment) {
            return new HRDGen3(projectASTDiff, generated, info, experiment);
        }
    },
    Filtered {
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, BenchmarkCase info, IExperiment experiment) {
            return new HRDGen2(projectASTDiff, generated, info, experiment);
        }
    },

    Experimental{
        @Override
        public HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff, ASTDiff generated, BenchmarkCase info, IExperiment experiment) {
            return new HRDGen3(projectASTDiff, generated, info, experiment);
        }
    };

    public abstract HumanReadableDiffGenerator getGenerator(ProjectASTDiff projectASTDiff,
                                                            ASTDiff generated,
                                                            BenchmarkCase info,
                                                            IExperiment experiment);

}
