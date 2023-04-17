package benchmark.oracle.models;

import java.io.Serializable;
import java.util.List;

public class HumanReadableDiff implements Serializable {
    List<AbstractMapping> matchedElements;
    List<AbstractMapping> mappings;

    public HumanReadableDiff(List<AbstractMapping> matchedElements, List<AbstractMapping> mappings) {
        this.matchedElements = matchedElements;
        this.mappings = mappings;
    }

    public HumanReadableDiff() {

    }

    public List<AbstractMapping> getMatchedElements() {

        return matchedElements;
    }

    public void setMatchedElements(List<AbstractMapping> matchedElements) {
        this.matchedElements = matchedElements;
    }

    public List<AbstractMapping> getMappings() {
        return mappings;
    }

    public void setMappings(List<AbstractMapping> mappings) {
        this.mappings = mappings;
    }
}
