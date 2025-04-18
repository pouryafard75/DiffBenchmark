package benchmark.models;

import benchmark.data.diffcase.IBenchmarkCase;

import java.util.function.BiFunction;

public interface IGenerator<U,R> extends BiFunction<IBenchmarkCase, U, R> {
}

