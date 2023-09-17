package benchmark.oracle.generators;

import benchmark.utils.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.util.*;

import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {

    private final Configuration configuration;
    private Map<String, String> fileContentsBefore;
    private Map<String, String> fileContentsCurrent;

    public BenchmarkHumanReadableDiffGenerator(Configuration current){
        this.configuration = current;
    }
    public void generate() throws Exception {
        for (CaseInfo info : configuration.getAllCases()) {
            this.writeActiveTools(info, configuration.getOutputFolder());
        }
        System.out.println("Finished generating human readable diffs...");
    }
    private void writeActiveTools(CaseInfo info, String output_folder) throws Exception {
        String repo = info.getRepo();
        String commit = info.getCommit();
        System.out.println("Started for " + repo + " " + commit);
        ProjectASTDiff projectASTDiff = runWhatever(repo, commit);
        this.fileContentsBefore = projectASTDiff.getFileContentsBefore();
        this.fileContentsCurrent = projectASTDiff.getFileContentsAfter();
        Set<ASTDiff> astDiffs = projectASTDiff.getDiffSet();
        for (ASTDiff astDiff : astDiffs) {
            //----------------------------------\\
            for (ASTDiffTool tool : configuration.getActiveTools()) {
                String toolName = tool.name(); //In case we later introduce a map from tool's name to tool's path
                String toolPath = tool.name(); //In case we later introduce a map from tool's name to tool's path
                DiffToolFactory factory = tool.getFactory();
                ASTDiff generated = factory.getASTDiff(projectASTDiff, astDiff, info, configuration);
                GenerationStrategy generationStrategy = configuration.getGenerationStrategy();
                HumanReadableDiffGenerator humanReadableDiffGenerator = generationStrategy.getGenerator(projectASTDiff, generated, info, configuration);
                humanReadableDiffGenerator.write(output_folder,astDiff.getSrcPath(),toolPath);
            }
        }
        System.out.println("Finished for " + repo + " " + commit);
    }
}
