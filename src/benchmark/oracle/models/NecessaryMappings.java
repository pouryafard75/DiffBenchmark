package benchmark.oracle.models;

import java.util.Set;
import java.util.TreeSet;

import static benchmark.oracle.models.HumanReadableDiff.abstractMappingComparator;

/* Created by pourya on 2023-08-09 4:58 p.m. */
public class NecessaryMappings {
    Set<AbstractMapping> matchedElements;
    Set<AbstractMapping> mappings;
    public NecessaryMappings(){
        mappings = new TreeSet<>(abstractMappingComparator);
        matchedElements = new TreeSet<>(abstractMappingComparator);
    }
    public Set<AbstractMapping> getMatchedElements() {
        return matchedElements;
    }

    public void setMatchedElements(Set<AbstractMapping> matchedElements) {
        this.matchedElements = matchedElements;
    }

    public Set<AbstractMapping> getMappings() {
        return mappings;
    }

    public void setMappings(Set<AbstractMapping> mappings) {
        this.mappings = mappings;
    }

}
