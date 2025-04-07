package benchmark.generators.hrd;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.models.IGenerationStrategy;
import benchmark.models.hrd.HumanReadableDiff;
import org.refactoringminer.astDiff.models.ASTDiff;

public enum GenerationStrategy implements IGenerationStrategy {
    NO_COMMENTS_AND_JAVADOCS {
        @Override
        public HumanReadableDiff apply(IBenchmarkCase benchmarkCase, ASTDiff current) {
            return new HRDGen3(benchmarkCase, current).getResult();
        }
    },
    Filtered {
        @Override
        public HumanReadableDiff apply(IBenchmarkCase benchmarkCase, ASTDiff current) {
            return new HRDGen2(benchmarkCase, current).getResult();
        }
    },
    NonFiltered_CommentsOnly {
        @Override
        public HumanReadableDiff apply(IBenchmarkCase benchmarkCase, ASTDiff current) {
            return new HRDGen3(benchmarkCase, current, FilterDuringGeneration.COMMENTS_ONLY).getResult();
        }
    },
    EXP_ALL_COMMENTS {
        @Override
        public HumanReadableDiff apply(IBenchmarkCase benchmarkCase, ASTDiff current) {
            return new HRDGenOnlyComments(benchmarkCase, current).getResult();
        }
    },
    ;

}
