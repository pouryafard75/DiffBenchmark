package rq;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.JavaMatchers;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import gr.uom.java.xmi.UMLModel;
import gr.uom.java.xmi.diff.UMLModelDiff;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ProjectASTDiffer;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import shaded.com.github.gumtreediff.actions.ActionGenerator;
import shaded.com.github.gumtreediff.actions.model.Action;
import shaded.com.github.gumtreediff.gen.TreeGenerator;
import shaded.com.github.gumtreediff.matchers.OptimizedVersions;
import shaded.com.github.gumtreediff.tree.ITree;
import shaded.com.github.gumtreediff.tree.TreeContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;
import static benchmark.utils.Helpers.runWhatever;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;
import static org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl.createModelForASTDiff;

/* Created by pourya on 2023-11-23 9:53 p.m. */

/***
 * What is the execution time of each tool?
 */
public class RQ8 implements RQ{
    private int numberOfExecutions = 5;
    static Map<CaseInfo, ProjectASTDiff> resourceMap = new HashMap<>();

    public void setNumberOfExecutions(int numberOfExecutions) {
        this.numberOfExecutions = numberOfExecutions;
    }

    @Override
    public void run(Configuration[] confs) throws Exception {
        for (Configuration conf : confs) {
            rq8(conf, conf.getName() + "-exe.csv", numberOfExecutions);
        }
    }
    public static ProjectASTDiff customAPI(Map<String, String> fileContentsBefore, Map<String, String> fileContentsCurrent) throws Exception {
        UMLModel parentUMLModel = createModelForASTDiff(fileContentsBefore, populateDirectories(fileContentsBefore));
        UMLModel currentUMLModel = createModelForASTDiff(fileContentsCurrent, populateDirectories(fileContentsCurrent));
        UMLModelDiff modelDiff = parentUMLModel.diff(currentUMLModel);
        ProjectASTDiffer differ = new ProjectASTDiffer(modelDiff, fileContentsBefore, fileContentsCurrent);
        return differ.getProjectASTDiff();
    }

    private static Set<String> populateDirectories(Map<String, String> fileContents) {
        Set<String> repositoryDirectories = new LinkedHashSet<>();
        for(String path : fileContents.keySet()) {
            String directory = new String(path);
            while(directory.contains("/")) {
                directory = directory.substring(0, directory.lastIndexOf("/"));
                repositoryDirectories.add(directory);
            }
        }
        return repositoryDirectories;
    }
    public static void rq8(Configuration config, String destinPath, int numOfExe) throws Exception {
        System.out.println("Configuration loaded.");
        populateResourceMap(config);
        System.out.println("Resource map populated.");
        List<ExeTimeRecord> result = new ArrayList<>();
        int completed = 0;
        for (CaseInfo info : config.getAllCases()) {
            System.out.println("Working on: " + info.makeURL());
            result.add(executionTimeForEachCase(info, numOfExe));
            completed++;
            System.out.println("Completed: " + completed + " out of " + config.getAllCases().size());
        }
        writeResults(result, destinPath);
    }

    private static void writeResults(List<ExeTimeRecord> result, String filePath) {
        try (FileWriter writer = new FileWriter(filePath)) {
            // Write the CSV header
            writer.append("url,RMD,GTG,GTS,IJM,MTD,GT2\n");
            for (ExeTimeRecord record : result) {
                writer.append(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
                        record.url, record.RMD, record.GTG, record.GTS, record.IJM, record.MTD, record.GT2));
            }
            System.out.println("CSV file has been created successfully!");
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error writing to the CSV file: " + e.getMessage());
        }
    }


    private static ExeTimeRecord executionTimeForEachCase(CaseInfo info, int numOfExe) throws Exception {
        ProjectASTDiff projectASTDiff = resourceMap.get(info);
        float RMD_time = 0;
        float GTG_time = 0;
        float GTS_time = 0;
        float IJM_time = 0;
        float MTD_time = 0;
        float GT2_time = 0;
        for (int i = 0; i < numOfExe; i++)
        {
            if (i == 0) {
                for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
                    String srcContents = projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath());
                    String dstContents = projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath());
                    getGTXTime(srcContents, dstContents, new CompositeMatchers.ClassicGumtree());
                    getGTXTime(srcContents, dstContents, new CompositeMatchers.SimpleGumtree());
                    getTime(srcContents, dstContents, JavaMatchers.IterativeJavaMatcher_V2.class, new OptimizedJdtTreeGenerator());
                    if (astDiff.getSrcPath().equals("core/src/processing/core/PApplet.java")) //Since this case cause the java heap space, we decided to run this case with the OptimizedJDTGenerator
                        getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new OptimizedJdtTreeGenerator());
                    else
                        getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
                    getTime(srcContents, dstContents, shaded.com.github.gumtreediff.matchers.CompositeMatchers.ClassicGumtree.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
                }
                diffTime(info);
            }
            else{
                for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
                    String srcContents = projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath());
                    String dstContents = projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath());
                    GTG_time += getGTXTime(srcContents, dstContents, new CompositeMatchers.ClassicGumtree());
                    GTS_time += getGTXTime(srcContents, dstContents, new CompositeMatchers.SimpleGumtree());
                    IJM_time += getTime(srcContents, dstContents, JavaMatchers.IterativeJavaMatcher_V2.class, new OptimizedJdtTreeGenerator());
                    if (astDiff.getSrcPath().equals("core/src/processing/core/PApplet.java")) //Since this case cause the java heap space, we decided to run this case with the OptimizedJDTGenerator
                        MTD_time += getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new OptimizedJdtTreeGenerator());
                    else
                        MTD_time += getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
                    GT2_time += getTime(srcContents, dstContents, shaded.com.github.gumtreediff.matchers.CompositeMatchers.ClassicGumtree.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
                }
                RMD_time += diffTime(info);
            }
        }
        RMD_time = RMD_time / (numOfExe - 1);
        GTG_time = GTG_time / (numOfExe - 1);
        GTS_time = GTS_time / (numOfExe - 1);
        IJM_time = IJM_time / (numOfExe - 1);
        MTD_time = MTD_time / (numOfExe - 1);
        GT2_time = GT2_time / (numOfExe - 1);

        return new ExeTimeRecord(info.makeURL(),
                RMD_time,
                GTG_time,
                GTS_time,
                IJM_time,
                MTD_time,
                GT2_time);
    }

    private static long getTime(String srcContents, String dstContents, Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> matcherType, TreeGenerator gen) throws IOException {
        long start = System.currentTimeMillis();
        TreeContext srcContext = gen.generateFromString(srcContents);
        TreeContext dstContext = gen.generateFromString(dstContents);
        ITree srcITree = srcContext.getRoot();
        ITree dstITree = dstContext.getRoot();
        shaded.com.github.gumtreediff.matchers.Matcher m =
                new MatcherFactory(matcherType).createMatcher(srcITree, dstITree);
        m.match();
        ActionGenerator g = new ActionGenerator(srcContext.getRoot(), dstContext.getRoot(), m.getMappings());
        g.generate();
        List<Action> actions = g.getActions();
        long end = System.currentTimeMillis();
        return end - start;
    }

    private static long getGTXTime(String srcContents, String dstContents, CompositeMatchers.CompositeMatcher matcher) throws IOException {
        long start = System.currentTimeMillis();
        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContents).getRoot();
        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContents).getRoot();
        MappingStore match = matcher.match(srcTree, dstTree);
        new SimplifiedChawatheScriptGenerator().computeActions(match);
        long finish = System.currentTimeMillis();
        return finish - start;
    }

    private static void populateResourceMap(Configuration config) throws Exception {
        int loaded = 0;
        for (CaseInfo caseInfo : config.getAllCases())
        {
            resourceMap.put(caseInfo, runWhatever(caseInfo.getRepo(), caseInfo.getCommit()));
            loaded++;
            System.out.println(loaded + " out of " + config.getAllCases().size() + " loaded.");
        }
    }
    public static long diffTimeDefects4j(String projectDir, String bugID) throws Exception {
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtDirectories(
                Path.of(getBeforeDir(projectDir, bugID)),
                Path.of(getAfterDir(projectDir, bugID)));
        long start = System.currentTimeMillis();
        customAPI(projectASTDiff.getFileContentsBefore(), projectASTDiff.getFileContentsAfter());
        long end = System.currentTimeMillis();
        return end - start;
    }
    public static long diffTimeCommits(String repo, String commit) throws Exception {
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(repo, commit, new File(ORACLE_DIR));
        long start = System.currentTimeMillis();
        projectASTDiff =  customAPI(projectASTDiff.getFileContentsBefore(), projectASTDiff.getFileContentsAfter());
        long end = System.currentTimeMillis();
        return end - start;
    }
    public static long diffTime(CaseInfo info) throws Exception {
        if (info.getRepo().contains(".git"))
            return diffTimeCommits(info.getRepo(), info.getCommit());
        else
            return diffTimeDefects4j(info.getRepo(), info.getCommit());
    }


    record ExeTimeRecord
            (String url,
             float RMD,
             float GTG,
             float GTS,
             float IJM,
             float MTD,
             float GT2)
    {}
}
