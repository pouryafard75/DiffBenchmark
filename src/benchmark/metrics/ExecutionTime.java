//package benchmark.metrics;
//
//import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
//import at.aau.softwaredynamics.matchers.JavaMatchers;
//import at.aau.softwaredynamics.matchers.MatcherFactory;
//import benchmark.utils.CaseInfo;
//import benchmark.utils.Configuration.Configuration;
//import benchmark.utils.Configuration.ConfigurationFactory;
//import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
//import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
//import com.github.gumtreediff.matchers.CompositeMatchers;
//import com.github.gumtreediff.matchers.MappingStore;
//import com.github.gumtreediff.tree.Tree;
//import org.refactoringminer.api.GitService;
//import org.refactoringminer.astDiff.actions.ASTDiff;
//import org.refactoringminer.astDiff.actions.ProjectASTDiff;
//import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
//import org.refactoringminer.util.GitServiceImpl;
//import shaded.com.github.gumtreediff.actions.ActionGenerator;
//import shaded.com.github.gumtreediff.actions.model.Action;
//import shaded.com.github.gumtreediff.gen.TreeGenerator;
//import shaded.com.github.gumtreediff.matchers.OptimizedVersions;
//import shaded.com.github.gumtreediff.tree.ITree;
//import shaded.com.github.gumtreediff.tree.TreeContext;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.io.Serializable;
//import java.nio.file.Path;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import static benchmark.utils.Helpers.REPOS;
//import static benchmark.utils.Helpers.runWhatever;
//import static benchmark.utils.PathResolver.getAfterDir;
//import static benchmark.utils.PathResolver.getBeforeDir;
//
//
///* Created by pourya on 2023-04-02 7:49 p.m. */
//public class ExecutionTime {
//    private static final int numberOfExecutions = 5;
//    static Map<CaseInfo,ProjectASTDiff> resourceMap = new HashMap<>();
//
//    public static void main(String[] args) throws Exception {
//        Configuration config = ConfigurationFactory.defects4j();
//        System.out.println("Configurations loaded.");
//        populateResourceMap(config);
//        System.out.println("Resource map populated.");
////        if (true) return;
//        List<ExeTimeRecord> result = new ArrayList<>();
//        int completed = 0;
//        for (CaseInfo info : config.getAllCases()) {
//            System.out.println("Working on: " + info.makeURL());
//            result.add(executionTimeForEachCase(info));
//            completed++;
//
//            System.out.println("Completed: " + completed + " out of " + config.getAllCases().size());
//
//        }
//        writeResults(result,"exeTime.csv");
//    }
//
//    private static void writeResults(List<ExeTimeRecord> result, String filePath) {
//        try (FileWriter writer = new FileWriter(filePath)) {
//            // Write the CSV header
//            writer.append("url,RMD,GTG,GTS,IJM,MTD,GT2\n");
//            for (ExeTimeRecord record : result) {
//                writer.append(String.format("%s,%.2f,%.2f,%.2f,%.2f,%.2f,%.2f\n",
//                        record.url, record.RMD, record.GTG, record.GTS, record.IJM, record.MTD, record.GT2));
//            }
//            System.out.println("CSV file has been created successfully!");
//        } catch (IOException e) {
//            e.printStackTrace();
//            System.err.println("Error writing to the CSV file: " + e.getMessage());
//        }
//    }
//
//
//    private static ExeTimeRecord executionTimeForEachCase(CaseInfo info) throws Exception {
//        ProjectASTDiff projectASTDiff = resourceMap.get(info);
//        float RMD_time = 0;
//        float GTG_time = 0;
//        float GTS_time = 0;
//        float IJM_time = 0;
//        float MTD_time = 0;
//        float GT2_time = 0;
//        for (int i = 0; i < numberOfExecutions; i++)
//        {
//            if (i == 0) {
//                for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
//                    String srcContents = projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath());
//                    String dstContents = projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath());
//                    getGTXTime(srcContents, dstContents, new CompositeMatchers.ClassicGumtree());
//                    getGTXTime(srcContents, dstContents, new CompositeMatchers.SimpleGumtree());
//                    getTime(srcContents, dstContents, JavaMatchers.IterativeJavaMatcher_V2.class, new OptimizedJdtTreeGenerator());
//                    if (astDiff.getSrcPath().equals("core/src/processing/core/PApplet.java")) //Since this case cause the java heap space, we decided to run this case with the OptimizedJDTGenerator
//                        getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new OptimizedJdtTreeGenerator());
//                    else
//                        getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
//                    getTime(srcContents, dstContents, shaded.com.github.gumtreediff.matchers.CompositeMatchers.ClassicGumtree.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
//                }
//                getRMTime(info);
//            }
//            else{
//                for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
//                    String srcContents = projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath());
//                    String dstContents = projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath());
//                    GTG_time += getGTXTime(srcContents, dstContents, new CompositeMatchers.ClassicGumtree());
//                    GTS_time += getGTXTime(srcContents, dstContents, new CompositeMatchers.SimpleGumtree());
//                    IJM_time += getTime(srcContents, dstContents, JavaMatchers.IterativeJavaMatcher_V2.class, new OptimizedJdtTreeGenerator());
//                    if (astDiff.getSrcPath().equals("core/src/processing/core/PApplet.java")) //Since this case cause the java heap space, we decided to run this case with the OptimizedJDTGenerator
//                        MTD_time += getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new OptimizedJdtTreeGenerator());
//                    else
//                        MTD_time += getTime(srcContents, dstContents, OptimizedVersions.MtDiff.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
//                    GT2_time += getTime(srcContents, dstContents, shaded.com.github.gumtreediff.matchers.CompositeMatchers.ClassicGumtree.class, new shaded.com.github.gumtreediff.gen.jdt.JdtTreeGenerator());
//                }
//                RMD_time += getRMTime(info);
//            }
//        }
//        RMD_time = RMD_time / (numberOfExecutions - 1);
//        GTG_time = GTG_time / (numberOfExecutions - 1);
//        GTS_time = GTS_time / (numberOfExecutions - 1);
//        IJM_time = IJM_time / (numberOfExecutions - 1);
//        MTD_time = MTD_time / (numberOfExecutions - 1);
//        GT2_time = GT2_time / (numberOfExecutions - 1);
//
//        return new ExeTimeRecord(info.makeURL(),
//                RMD_time ,
//                GTG_time ,
//                GTS_time ,
//                IJM_time ,
//                MTD_time ,
//                GT2_time);
//    }
//
//    private static long getTime(String srcContents, String dstContents, Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> matcherType, TreeGenerator gen) throws IOException {
//        long start = System.currentTimeMillis();
//        TreeContext srcContext = gen.generateFromString(srcContents);
//        TreeContext dstContext = gen.generateFromString(dstContents);
//        ITree srcITree = srcContext.getRoot();
//        ITree dstITree = dstContext.getRoot();
//        shaded.com.github.gumtreediff.matchers.Matcher m =
//                new MatcherFactory(matcherType).createMatcher(srcITree, dstITree);
//        m.match();
//        ActionGenerator g = new ActionGenerator(srcContext.getRoot(), dstContext.getRoot(), m.getMappings());
//        g.generate();
//        List<Action> actions = g.getActions();
//        long end = System.currentTimeMillis();
//        return end - start;
//    }
//
//    private static long getGTXTime(String srcContents, String dstContents, CompositeMatchers.CompositeMatcher matcher) throws IOException {
//        long start = System.currentTimeMillis();
//        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContents).getRoot();
//        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContents).getRoot();
//        MappingStore match = matcher.match(srcTree, dstTree);
//        new SimplifiedChawatheScriptGenerator().computeActions(match);
//        long finish = System.currentTimeMillis();
//        return finish - start;
//    }
//
//    private static long getRMTime(CaseInfo info) throws Exception {
//        long RM_time;
//        if (info.getRepo().contains(".git")) {
//            GitService gitService = new GitServiceImpl();
//            RM_time = new GitHistoryRefactoringMinerImpl().diffTime(info.getRepo(),info.getCommit(),new File(REPOS));
//        }
//        else {
//            Path beforePath = Path.of(getBeforeDir(info.getRepo(), info.getCommit()));
//            Path afterPath = Path.of(getAfterDir(info.getRepo(), info.getCommit()));
//            RM_time = new GitHistoryRefactoringMinerImpl().diffTime
//                    (beforePath,afterPath);
//        }
//        return RM_time;
//    }
//
//    private static void populateResourceMap(Configuration config) throws Exception {
//        int loaded = 0;
//        for (CaseInfo caseInfo : config.getAllCases())
//        {
//            resourceMap.put(caseInfo, runWhatever(caseInfo.getRepo(), caseInfo.getCommit()));
//            loaded++;
//            System.out.println(loaded + " out of " + config.getAllCases().size() + " loaded.");
//        }
//    }
//}
//
//class ExeTimeRecord implements Serializable {
//    public String url;
//    public float RMD;
//    public float GTG;
//    public float GTS;
//    public float IJM;
//    public float MTD;
//    public float GT2;
//
//    public ExeTimeRecord(String url, float RMD, float GTG, float GTS, float IJM, float MTD, float GT2) {
//        this.url = url;
//        this.RMD = RMD;
//        this.GTG = GTG;
//        this.GTS = GTS;
//        this.IJM = IJM;
//        this.MTD = MTD;
//        this.GT2 = GT2;
//    }
//
//    public ExeTimeRecord() {
//    }
//}
