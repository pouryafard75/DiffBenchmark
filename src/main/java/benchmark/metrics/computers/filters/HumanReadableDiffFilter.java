package benchmark.metrics.computers.filters;

import benchmark.models.hrd.AbstractMapping;
import benchmark.models.hrd.HumanReadableDiff;

import java.util.function.Predicate;

public interface HumanReadableDiffFilter {
    HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack);

    static HumanReadableDiffFilter byPredicate(Predicate<AbstractMapping> condition) {
        return (original, slack) -> FilterUtils.apply(original, condition);
    }
}
