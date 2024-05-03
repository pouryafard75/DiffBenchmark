package benchmark.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.*;

import static benchmark.models.HumanReadableDiff.abstractMappingComparator;

/* Created by pourya on 2023-08-09 4:58 p.m. */
public class NecessaryMappings {
    @JsonDeserialize(as = HashSet.class)
    Collection<AbstractMapping> matchedElements;
    @JsonDeserialize(as = HashSet.class)
    Collection<AbstractMapping> mappings;


    public NecessaryMappings(){
        mappings = new TreeSet<>(abstractMappingComparator);
        matchedElements = new TreeSet<>(abstractMappingComparator);
    }

    public NecessaryMappings(Collection<AbstractMapping> matchedElements, Collection<AbstractMapping> mappings) {
        this.matchedElements = matchedElements;
        this.mappings = mappings;
    }

    public Collection<AbstractMapping> getMatchedElements() {
        return matchedElements;
    }
    public Collection<AbstractMapping> getMappings() {
        return mappings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NecessaryMappings that = (NecessaryMappings) o;
        return Objects.equals(matchedElements, that.matchedElements) && Objects.equals(mappings, that.mappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(matchedElements, mappings);
    }
}
