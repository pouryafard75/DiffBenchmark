package benchmark.metrics.computers.filters;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import org.refactoringminer.astDiff.matchers.Constants;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public enum MappingsTypeFilter {
    NO_FILTER(abstractMapping -> true),
    TYPE_DECL_ONLY(getFilterByType(Constants.TYPE_DECLARATION)),
    METHOD_DECL_ONLY(getFilterByType(Constants.METHOD_DECLARATION)),
    FIELD_DECL_ONLY(getFilterByType(Constants.FIELD_DECLARATION)),
    ENUM_DECL_ONLY(getFilterByType(Constants.ENUM_DECLARATION));

    private final Predicate<AbstractMapping> typeFilter;

    public Collection<AbstractMapping> apply(Collection<AbstractMapping> input){
        return input.stream().filter(typeFilter).collect(Collectors.toSet());
    }
    public NecessaryMappings apply(NecessaryMappings input){
        return new NecessaryMappings(apply(input.getMatchedElements()),apply(input.getMappings()));
    }
    public HumanReadableDiff apply(HumanReadableDiff humanReadableDiff){
        Map<String, NecessaryMappings> newMap = new HashMap<>();
        for (Map.Entry<String, NecessaryMappings> entry : humanReadableDiff.getInterFileMappings().entrySet()) {
            newMap.put(entry.getKey(), apply(entry.getValue()));
        }
        return new HumanReadableDiff(apply(humanReadableDiff.getIntraFileMappings()), newMap);
    }

    MappingsTypeFilter(Predicate<AbstractMapping> filter) {
        this.typeFilter = filter;
    }
    private static Predicate<AbstractMapping> getFilterByType(String type)
    {
        return abstractMapping -> abstractMapping.getLeftInfo().contains(type) || abstractMapping.getRightInfo().contains(type);
    }
}
