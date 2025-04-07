package benchmark.metrics.computers.filters;

import benchmark.models.hrd.AbstractMapping;
import benchmark.models.hrd.HumanReadableDiff;
import benchmark.models.hrd.NecessaryMappings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/* Created by pourya on 2024-10-03*/
public class FilterUtils {

    public static Collection<AbstractMapping> apply(Collection<AbstractMapping> input, Predicate<AbstractMapping> typeFilter){
        return input.stream().filter(typeFilter).collect(Collectors.toSet());
    }
    public static NecessaryMappings apply(NecessaryMappings input, Predicate<AbstractMapping> typeFilter){
        return new NecessaryMappings(apply(input.getMatchedElements(), typeFilter),apply(input.getMappings(), typeFilter));
    }
    public static HumanReadableDiff apply(HumanReadableDiff humanReadableDiff, Predicate<AbstractMapping> typeFilter){
        Map<String, NecessaryMappings> newMap = new HashMap<>();
        for (Map.Entry<String, NecessaryMappings> entry : humanReadableDiff.getInterFileMappings().entrySet()) {
            newMap.put(entry.getKey(), apply(entry.getValue(), typeFilter));
        }
        return new HumanReadableDiff(apply(humanReadableDiff.getIntraFileMappings(), typeFilter), newMap);
    }
    public static Predicate<AbstractMapping> getFilterByType(String type)
    {
        String finalType = type + "["; //To avoid matching with TYPE_DECLARATION_STATEMENT
        return abstractMapping -> abstractMapping.getLeftInfo().contains(finalType) || abstractMapping.getRightInfo().contains(finalType);
    }
}
