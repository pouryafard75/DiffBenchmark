package benchmark.metrics;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;
import shaded.com.github.gumtreediff.actions.ActionGenerator;
import shaded.com.github.gumtreediff.actions.model.Action;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.Matcher;
import shaded.com.github.gumtreediff.tree.ITree;
import shaded.com.github.gumtreediff.tree.TreeContext;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static benchmark.utils.Configuration.Configuration.REPOS;
import static benchmark.utils.Helpers.runWhatever;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;


/* Created by pourya on 2023-04-02 7:49 p.m. */
public class ExecutionTime {
    private static final int numberOfExecutions = 5;
    static Map<CaseInfo,ProjectASTDiff> resourceMap = new HashMap<>();
    static List<Long> rmdAllTimes = new ArrayList<>();
    static List<Long> gtgAllTimes = new ArrayList<>();
    static List<Long> gtsAllTimes = new ArrayList<>();
    static List<Long> ijmAllTimes = new ArrayList<>();
    static List<Long> mtdAllTimes = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        Configuration config = ConfigurationFactory.defects4j();
        System.out.println("Configurations loaded.");
        populateResourceMap(config);
        System.out.println("Resource map populated.");
        int completed = 0;
        for (CaseInfo info : config.getAllCases()) {
            executionTimeForEachCase(info);
            completed++;
            System.out.println("Completed: " + completed + " out of " + config.getAllCases().size());

        }
        writeResults();
    }

    private static void writeResults() {
        writeListToFile(rmdAllTimes, "rmdAllTimes.txt");
        writeListToFile(gtgAllTimes, "gtgAllTimes.txt");
        writeListToFile(gtsAllTimes, "gtsAllTimes.txt");
        writeListToFile(ijmAllTimes, "ijmAllTimes.txt");
        writeListToFile(mtdAllTimes, "mtdAllTimes.txt");
    }

    private static void writeListToFile(List<Long> results, String filePath) {
        try {
            // Create a FileWriter to write to the file
            FileWriter fileWriter = new FileWriter(filePath);

            // Create a BufferedWriter to improve write performance
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            // Iterate through the list of numbers and write each one to the file
            for (Long number : results) {
                bufferedWriter.write(number.toString());
                bufferedWriter.newLine(); // Add a newline character to separate numbers
            }

            // Close the BufferedWriter and FileWriter
            bufferedWriter.close();
            fileWriter.close();
            System.out.println("Numbers have been written to the file.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void executionTimeForEachCase(CaseInfo info) throws Exception {
        ProjectASTDiff projectASTDiff = resourceMap.get(info);
        for (int i = 0; i < numberOfExecutions; i++) {
            long RM_time = getRMTime(info);
            long GTG_time = 0;
            long GTS_time = 0;
            long IJM_time = 0;
            long MTD_time = 0;
            for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
                String srcContents = projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath());
                String dstContents = projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath());
                GTG_time += getGTGTime(srcContents, dstContents);
                GTS_time += getGTSTime(srcContents, dstContents);
                IJM_time += getIJMTime(srcContents, dstContents);
                MTD_time += getMTDTime(srcContents, dstContents);
            }
            if (i == 0) continue;
            rmdAllTimes.add(RM_time);
            gtgAllTimes.add(GTG_time);
            gtsAllTimes.add(GTS_time);
            ijmAllTimes.add(IJM_time);
            mtdAllTimes.add(MTD_time);
        }
    }

    private static long getIJMTime(String srcContents, String dstContents) throws IOException {
        Class<? extends Matcher> matcherType = at.aau.softwaredynamics.matchers.JavaMatchers.IterativeJavaMatcher_V2.class;
        long start = System.currentTimeMillis();
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
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

    private static long getMTDTime(String srcContents, String dstContents) throws IOException {
        Class<? extends Matcher> matcherType = shaded.com.github.gumtreediff.matchers.OptimizedVersions.MtDiff.class;
        long start = System.currentTimeMillis();
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
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

    private static long getGTGTime(String srcContents, String dstContents) throws IOException {
        long start = System.currentTimeMillis();
        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContents).getRoot();
        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContents).getRoot();
        MappingStore match = new CompositeMatchers.ClassicGumtree().match(srcTree, dstTree);
        new SimplifiedChawatheScriptGenerator().computeActions(match);
        long finish = System.currentTimeMillis();
        return finish - start;
    }
    private static long getGTSTime(String srcContents, String dstContents) throws IOException {
        long start = System.currentTimeMillis();
        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContents).getRoot();
        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContents).getRoot();
        MappingStore match = new CompositeMatchers.SimpleGumtree().match(srcTree, dstTree);
        new SimplifiedChawatheScriptGenerator().computeActions(match);
        long finish = System.currentTimeMillis();
        return finish - start;
    }

    private static long getRMTime(CaseInfo info) throws Exception {
        long RM_time;
        if (info.getRepo().contains(".git")) {
            GitService gitService = new GitServiceImpl();
            String repoFolder = info.getRepo().substring(info.getRepo().lastIndexOf("/"), info.getRepo().indexOf(".git"));
            Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, info.getRepo());
            RM_time = new GitHistoryRefactoringMinerImpl().diffTime(repository, info.getCommit());
        }
        else {
            Path beforePath = Path.of(getBeforeDir(info.getRepo(), info.getCommit()));
            Path afterPath = Path.of(getAfterDir(info.getRepo(), info.getCommit()));
            RM_time = new GitHistoryRefactoringMinerImpl().diffTime
                    (beforePath,afterPath);
        }
        return RM_time;
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


}
