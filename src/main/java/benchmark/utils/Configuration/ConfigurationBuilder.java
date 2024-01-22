package benchmark.utils.Configuration;

import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.CaseInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.Set;
import java.util.TreeSet;

public class ConfigurationBuilder {
    private String perfectDiffDir;
    private Set<CaseInfo> allCases = new TreeSet<>(Comparator.comparing(CaseInfo::makeURL));
    private Compatibility compatibility;
    private GenerationStrategy generationStrategy;
    private Set<ASTDiffTool> tools;

    public ConfigurationBuilder setTools(Set<ASTDiffTool> tools) {
        this.tools = tools;
        return this;
    }

    public ConfigurationBuilder setPerfectDiffDir(String perfectDiffDir) {
        this.perfectDiffDir = perfectDiffDir;
        return this;
    }

    public ConfigurationBuilder setAllCases(Set<CaseInfo> allCases) {
        this.allCases = allCases;
        return this;
    }
    public ConfigurationBuilder setAllCases(String[] casesPath) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        for (String path : casesPath) {
            Set<CaseInfo> loaded = mapper.readValue(new File(path), new TypeReference<Set<CaseInfo>>(){});
            this.allCases.addAll(loaded);
        }
        return this;
    }

    public ConfigurationBuilder setCompatibility(Compatibility compatibility) {
        this.compatibility = compatibility;
        return this;
    }

    public ConfigurationBuilder setGenerationStrategy(GenerationStrategy generationStrategy) {
        this.generationStrategy = generationStrategy;
        return this;
    }


    public Configuration createConfiguration() throws IOException {
        if (tools == null)
            return new Configuration(perfectDiffDir, allCases, compatibility, generationStrategy);
        else
            return new Configuration(perfectDiffDir, allCases, compatibility, generationStrategy, tools);
    }
}