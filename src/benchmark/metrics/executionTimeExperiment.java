//package benchmark.metrics;
//import benchmark.utils.CaseInfo;
//import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
//import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
//import com.github.gumtreediff.matchers.CompositeMatchers;
//import com.github.gumtreediff.matchers.MappingStore;
//import com.github.gumtreediff.tree.Tree;
//import org.eclipse.jdt.core.dom.AST;
//import org.eclipse.jdt.core.dom.ASTParser;
//
//
//import java.io.IOException;
//import java.nio.charset.StandardCharsets;
//import java.nio.file.DirectoryStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.util.HashMap;
//import java.util.Map;
//
//
//import static benchmark.utils.PathResolver.getAfterDir;
//import static benchmark.utils.PathResolver.getBeforeDir;
//
///* Created by pourya on 2023-09-10 9:32 p.m. */
//public class executionTimeExperiment {
//    public static void main(String[] args) throws IOException {
//
//        Map<String, String> fileContentsMapBefore = new HashMap<>();
//        Map<String, String> fileContentsMapAfter = new HashMap<>();
//        CaseInfo info = new CaseInfo("Chart", "1");
//
//        ASTParser astParser = ASTParser.newParser(AST.JLS19);
//
//        populateContents(info,fileContentsMapBefore,fileContentsMapAfter);
//
//        String srcContents = null,dstContents = null;
//        for (Map.Entry<String, String> stringStringEntry : fileContentsMapBefore.entrySet())
//            srcContents = stringStringEntry.getValue();
//        for (Map.Entry<String, String> stringStringEntry : fileContentsMapAfter.entrySet())
//            dstContents = stringStringEntry.getValue();
//
//        for (int i = 0; i < 5; i++) {
//            long start = System.currentTimeMillis();
//            Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContents).getRoot();
//            Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContents).getRoot();
//            MappingStore match = new CompositeMatchers.ClassicGumtree().match(srcTree, dstTree);
//            new SimplifiedChawatheScriptGenerator().computeActions(match);
//            long finish = System.currentTimeMillis();
//            System.out.println(finish - start);
//        }
//    }
//
//    private static void populateContents(CaseInfo info, Map<String, String> fileContentsMapBefore, Map<String, String> fileContentsMapAfter) {
//
//        Path beforePath = Path.of(getBeforeDir(info.getRepo(), info.getCommit()));
//        Path afterPath = Path.of(getAfterDir(info.getRepo(), info.getCommit()));
//        try {
//            getFileContentsMap(beforePath, fileContentsMapBefore);
//            getFileContentsMap(afterPath, fileContentsMapAfter);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//    public static void getFileContentsMap(Path dir, Map<String, String> fileContentsMap) throws IOException {
//        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
//            for (Path filePath : stream) {
//                if (Files.isRegularFile(filePath)) {
//                    String fileName = filePath.getFileName().toString();
//                    String fileContents = new String(Files.readAllBytes(filePath), StandardCharsets.UTF_8);
//                    fileContentsMap.put(fileName, fileContents);
//                }
//            }
//        }
//    }
//}
//
//// TODO:
//// Single program iterates through all the tools,
//// Then run ach case 5 times, discard the first, and pick the average.
//// Check the recent commit related to type change for imports (Revert)
//// 9fd13eb7f4b0840786e8c4a2cd1ad48cf0516d0b
//// Check commits which have a lot of ExtractAndMove (find cases)
//// Report the number containing the extract and move refactoring
//// Comment out the invocation to MappingOptimizer and check the results