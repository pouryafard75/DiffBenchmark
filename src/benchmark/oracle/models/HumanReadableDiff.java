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

    public NecessaryMappings intraFileMappings;
    public Map<String,NecessaryMappings> interFileMappings;

    public HumanReadableDiff() {
        intraFileMappings = new NecessaryMappings();
        interFileMappings = new HashMap<>();
    }
}
