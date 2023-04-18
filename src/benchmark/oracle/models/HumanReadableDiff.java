package benchmark.oracle.models;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

public class HumanReadableDiff implements Serializable {
    Set<AbstractMapping> matchedElements;
    Set<AbstractMapping> mappings;

    public HumanReadableDiff(Set<AbstractMapping> matchedElements, Set<AbstractMapping> mappings) {
        this.matchedElements = matchedElements;
        this.mappings = mappings;
    }

    public HumanReadableDiff() {

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
