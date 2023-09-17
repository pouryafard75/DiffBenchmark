package benchmark.oracle.models;

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
}
