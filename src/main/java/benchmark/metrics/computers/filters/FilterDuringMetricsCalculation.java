package benchmark.metrics.computers.filters;

import benchmark.models.AbstractMapping;
import org.refactoringminer.astDiff.utils.Constants;

import java.util.function.Predicate;

import static benchmark.metrics.computers.filters.FilterUtils.getFilterByType;

public enum FilterDuringMetricsCalculation implements CalculationFilter{
    NO_FILTER(abstractMapping -> true),
    TYPE_DECL_ONLY(getFilterByType(Constants.TYPE_DECLARATION)),
    METHOD_DECL_ONLY(getFilterByType(Constants.METHOD_DECLARATION)),
    FIELD_DECL_ONLY(getFilterByType(Constants.FIELD_DECLARATION)),
    ENUM_DECL_ONLY(getFilterByType(Constants.ENUM_DECLARATION)),
    PROGRAM_ELEMENTS(getFilterByType(Constants.TYPE_DECLARATION)
            .or(getFilterByType(Constants.METHOD_DECLARATION))
            .or(getFilterByType(Constants.FIELD_DECLARATION))
            .or(getFilterByType(Constants.ENUM_DECLARATION))),
    ;

    FilterDuringMetricsCalculation(Predicate<AbstractMapping> filter) {
        this.typeFilter = filter;
    }
    private final Predicate<AbstractMapping> typeFilter;
    

    @Override
    public boolean test(AbstractMapping abstractMapping) {
        return typeFilter.test(abstractMapping);
    }
}
