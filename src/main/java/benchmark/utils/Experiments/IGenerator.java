package benchmark.utils.Experiments;

import benchmark.data.diffcase.IBenchmarkCase;

public interface IGenerator<R>{
    R get(IBenchmarkCase benchmarkCase, IQuerySelector query);
}

