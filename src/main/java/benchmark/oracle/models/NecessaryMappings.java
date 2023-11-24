package benchmark.oracle.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.*;

import static benchmark.oracle.models.HumanReadableDiff.abstractMappingComparator;

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

}
