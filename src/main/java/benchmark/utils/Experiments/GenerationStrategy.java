package benchmark.utils.Experiments;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.hrd.HRDGen2;
import benchmark.generators.hrd.HRDGen3;
import benchmark.models.HumanReadableDiff;

public enum GenerationStrategy implements IGenerationStrategy {
    NonFiltered {
        @Override
        public HumanReadableDiff get(IBenchmarkCase benchmarkCase, IQuerySelector query) {
            return new HRDGen3(benchmarkCase, query).getResult();
        }
    },
    Filtered {
        @Override
        public HumanReadableDiff get(IBenchmarkCase benchmarkCase, IQuerySelector query) {
            return new HRDGen2(benchmarkCase, query).getResult();
        }
    },
}
