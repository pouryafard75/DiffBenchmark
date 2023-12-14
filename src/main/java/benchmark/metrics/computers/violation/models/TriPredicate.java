package benchmark.metrics.computers.violation.models;

/* Created by pourya on 2023-12-13 10:40 p.m. */

@FunctionalInterface
public interface TriPredicate<T, U, V> {
    boolean test(T t, U u, V v);
}
