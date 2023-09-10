package benchmark.oracle.generators;

import benchmark.oracle.generators.changeAPI.APIChanger;
import benchmark.oracle.generators.changeAPI.IJM;
import benchmark.oracle.generators.changeAPI.MTDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.github.gumtreediff.matchers.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static benchmark.utils.Configuration.REPOS;
import static benchmark.utils.Helpers.runWhatever;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {

    private final Configuration configuration;
    private Map<String, String> fileContentsBefore;
    private Map<String, String> fileContentsCurrent;

    public BenchmarkHumanReadableDiffGenerator(Configuration current){
        this.configuration = current;
    }
    public void generate() throws Exception {
        for (CaseInfo info : configuration.getAllCases())
            this.writeActiveTools(info, configuration.getOutputFolder());
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
            HumanReadableDiffGenerator perfectHDG =
                    new HumanReadableDiffGenerator(repo, commit, new PerfectDiff(astDiff.getSrcPath(),projectASTDiff,repo,commit, configuration).makeASTDiff(), fileContentsBefore, fileContentsCurrent);
            perfectHDG.write(output_folder,astDiff.getSrcPath(),Configuration.GOD);
            //----------------------------------\\
            for (String tool : Configuration.getActiveTools()) {
                String toolName = tool; //In case we later introduce a map from tool's name to tool's path
                String toolPath = tool; //In case we later introduce a map from tool's name to tool's path
                ASTDiff generated = generateBasedOnTool(tool, astDiff, projectASTDiff);
                HumanReadableDiffGenerator humanReadableDiffGenerator = new HumanReadableDiffGenerator(repo, commit, generated, fileContentsBefore, fileContentsCurrent);
                humanReadableDiffGenerator.write(output_folder,astDiff.getSrcPath(),toolPath);
            }
        }
        System.out.println("Finished for " + repo + " " + commit);
    }

    private ASTDiff generateBasedOnTool(String tool, ASTDiff rm_astDiff, ProjectASTDiff projectASTDiff) throws Exception {
        if      (tool.equals(Configuration.RMD)) return rm_astDiff;
        else if (tool.equals(Configuration.GTG)) return APIChanger.makeASTDiffFromMatcher(new CompositeMatchers.ClassicGumtree(),rm_astDiff);
        else if (tool.equals(Configuration.GTS)) return APIChanger.makeASTDiffFromMatcher(new CompositeMatchers.SimpleGumtree(), rm_astDiff);
        else if (tool.equals(Configuration.IJM)) return new IJM(projectASTDiff,rm_astDiff).makeASTDiff();
        else if (tool.equals(Configuration.MTD)) return new MTDiff(projectASTDiff,rm_astDiff).makeASTDiff();
        else throw new RuntimeException("Tool not found!");
    }
}
