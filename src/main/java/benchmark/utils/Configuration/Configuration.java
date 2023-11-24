package benchmark.utils.Configuration;

import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.CaseInfo;

import java.io.IOException;
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

    public Configuration(String perfectDiffDir, Set<CaseInfo> allCases, Compatibility compatibility, GenerationStrategy generationStrategy) throws IOException {
        this.perfectDiffDir = perfectDiffDir;
        this.allCases = allCases;
        this.compatibility = compatibility;
        this.generationStrategy = generationStrategy;
        populateTools();
    }
    private void populateTools() {
        activeTools.add(ASTDiffTool.GOD);
        activeTools.add(ASTDiffTool.TRV);
        activeTools.addAll(Arrays.asList(compatibility.getTools()));
    }
    public ASTDiffTool[] getActiveTools() {
        return activeTools.toArray(new ASTDiffTool[0]);
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
}



