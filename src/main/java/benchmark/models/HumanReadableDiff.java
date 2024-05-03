package benchmark.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class HumanReadableDiff implements Serializable {
    @JsonIgnore
    static Comparator<AbstractMapping> abstractMappingComparator = Comparator.comparing(AbstractMapping::getLeftOffset)
            .thenComparing(AbstractMapping::getRightOffset)
            .thenComparing(AbstractMapping::getLeftEndOffset)
            .thenComparing(AbstractMapping::getRightEndOffset);

    private NecessaryMappings intraFileMappings;
    private Map<String,NecessaryMappings> interFileMappings;

    public HumanReadableDiff() {
        this(new NecessaryMappings());
    }
    public HumanReadableDiff(NecessaryMappings necessaryMappings) {
        this(necessaryMappings, new HashMap<>());
    }

    public HumanReadableDiff(NecessaryMappings necessaryMappings, Map<String, NecessaryMappings> interFileMappings) {
        setIntraFileMappings(necessaryMappings);
        setInterFileMappings(interFileMappings);
    }

    public static void write(File file, HumanReadableDiff result) {
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter("    ", "\n"));
        try {
            String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            out = prettify(out);
            FileUtils.writeStringToFile(file, out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static String prettify(String out) {
        return out.replace(": [", ": [\n");
    }

    public static HumanReadableDiff makeEmpty() {
        return new HumanReadableDiff(new NecessaryMappings());
    }

    public static Comparator<AbstractMapping> getAbstractMappingComparator() {
        return abstractMappingComparator;
    }

    public static void setAbstractMappingComparator(Comparator<AbstractMapping> abstractMappingComparator) {
        HumanReadableDiff.abstractMappingComparator = abstractMappingComparator;
    }

    public NecessaryMappings getIntraFileMappings() {
        return intraFileMappings;
    }

    public void setIntraFileMappings(NecessaryMappings intraFileMappings) {
        this.intraFileMappings = intraFileMappings;
    }

    public Map<String, NecessaryMappings> getInterFileMappings() {
        return interFileMappings;
    }

    public void setInterFileMappings(Map<String, NecessaryMappings> interFileMappings) {
        this.interFileMappings = interFileMappings;
    }

    @JsonIgnore
    public boolean isEmpty() {
        return intraFileMappings.getMappings().isEmpty() && intraFileMappings.getMatchedElements().isEmpty() && interFileMappings.isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HumanReadableDiff that = (HumanReadableDiff) o;
        return Objects.equals(intraFileMappings, that.intraFileMappings) && Objects.equals(interFileMappings, that.interFileMappings);
    }

    @Override
    public int hashCode() {
        return Objects.hash(intraFileMappings, interFileMappings);
    }
}
