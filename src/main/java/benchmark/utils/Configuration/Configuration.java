package benchmark.utils.Configuration;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.CaseInfo;

import java.util.*;

/* Created by pourya on 2023-04-17 9:27 p.m. */
public class Configuration {
    private Set<ASTDiffTool> activeTools = new LinkedHashSet<>();
    private final String perfectDiffDir;
    private final Set<CaseInfo> allCases;
    private final Compatibility compatibility;
    private final GenerationStrategy generationStrategy;

    private String outputFolder = "output/";
    private String csvDestinationFile = "stats.csv";
    private String name = "no-name";


    public void setOutputFolder(String outputFolder) {
        this.outputFolder = outputFolder;
        if (!outputFolder.endsWith("/"))
            this.outputFolder += "/";
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void setCsvDestinationFile(String csvDestinationFile) {
        this.csvDestinationFile = csvDestinationFile;
    }



    public GenerationStrategy getGenerationStrategy() {
        return generationStrategy;
    }

    public Compatibility getCompatibility() {
        return compatibility;
    }

    public Configuration(String perfectDiffDir, Set<CaseInfo> allCases, Compatibility compatibility, GenerationStrategy generationStrategy) {
            this(
                perfectDiffDir,
                allCases,
                compatibility,
                generationStrategy,
                populateTools(compatibility)
            );
    }
    public Configuration(String perfectDiffDir, Set<CaseInfo> allCases, Compatibility compatibility, GenerationStrategy generationStrategy, Set<ASTDiffTool> activeTools) {
        this.perfectDiffDir = perfectDiffDir;
        this.allCases = allCases;
        this.compatibility = compatibility;
        this.generationStrategy = generationStrategy;
        this.activeTools = activeTools;
    }
    private static Set<ASTDiffTool> populateTools(Compatibility compatibility) {
        Set<ASTDiffTool> tools = new LinkedHashSet<>();
        tools.add(ASTDiffTool.GOD);
        tools.add(ASTDiffTool.TRV);
        tools.addAll(Arrays.asList(compatibility.getTools()));
        return tools;
    }
    public ASTDiffTool[] getActiveTools() {
        ASTDiffTool[] array = activeTools.toArray(new ASTDiffTool[0]);
        return array;
    }

    public void setActiveTools(Set<ASTDiffTool> activeTools) {
        this.activeTools = activeTools;
    }

    public String getPerfectDiffDir() {
        return perfectDiffDir;
    }

    public Set<CaseInfo> getAllCases() {
        return allCases;
    }

    public String getOutputFolder() {
        return outputFolder;
    }

    public String getCsvDestinationFile() {
        return csvDestinationFile;
    }

    public static String getMergedNames(Configuration[] configurations) {
        StringBuilder configNames = new StringBuilder();
        for (Configuration configuration : configurations) {
            configNames.append(configuration.getName()).append("-");
        }
        return configNames.toString();
    }
}



