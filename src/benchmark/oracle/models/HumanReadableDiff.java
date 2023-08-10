package benchmark.oracle.models;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.*;

public class HumanReadableDiff implements Serializable {
    @JsonIgnore
    public static Comparator<AbstractMapping> abstractMappingComparator = Comparator.comparing(AbstractMapping::getLeftOffset)
            .thenComparing(AbstractMapping::getRightOffset)
            .thenComparing(AbstractMapping::getLeftEndOffset)
            .thenComparing(AbstractMapping::getRightEndOffset);

    public NecessaryMappings fileMappings;
    public Map<String,NecessaryMappings> interfileMappings;

    public HumanReadableDiff() {
        fileMappings = new NecessaryMappings();
        interfileMappings = new HashMap<>();
    }
}
