package benchmark.oracle.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

public class HumanReadableDiff implements Serializable {
    Set<AbstractMapping> matchedElements;
    Set<AbstractMapping> mappings;
    @JsonIgnore
    Comparator<AbstractMapping> abstractMappingComparator = Comparator.comparing(AbstractMapping::getLeftOffset)
            .thenComparing(AbstractMapping::getRightOffset)
            .thenComparing(AbstractMapping::getLeftEndOffset)
            .thenComparing(AbstractMapping::getRightEndOffset);


    public HumanReadableDiff() {
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
