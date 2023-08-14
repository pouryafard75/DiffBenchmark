package benchmark.defects4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;

import org.apache.commons.lang3.tuple.Triple;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static benchmark.utils.PathResolver.getDefect4jDir;

/* Created by pourya on 2023-05-02 7:42 p.m. */
public class CompareMappings {
//    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CompareMappings.class);
    public final static CompositeMatchers.CompositeMatcher GREEDY = new CompositeMatchers.ClassicGumtree();
    public final static CompositeMatchers.CompositeMatcher SIMPLE = new CompositeMatchers.SimpleGumtree();
//    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        printStats();
//        printMultiMappings();
    }
    public static void printMultiMappings() throws IOException {
        List<String> beforeDirs = getDirPathList();
        Map<Defect4jCaseInfo, MappingIdenticalCheckResult> identicalCheckResultMap = new LinkedHashMap<>();
//        int cc = 0;
        for (String beforeDir : beforeDirs) {
//            cc += 1;
//            if (cc > 10) break;
            String projectDir = Path.of(beforeDir).getParent().getFileName().toString();
//            if (!projectDir.equals("JacksonXml")) continue;
            String bugID = Path.of(beforeDir).getFileName().toString();
            Defect4jCaseInfo defect4jCaseInfo = new Defect4jCaseInfo(projectDir, bugID);
            String afterDir = beforeDir.replace("before", "after");
            Set<ASTDiff> diffSet = new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(beforeDir), Path.of(afterDir)).getDiffSet();
            for (ASTDiff astDiff : diffSet) {
                if (astDiff.getAllMappings().dstToSrcMultis().size() > 0 ) {
                    System.out.println(beforeDir);
                    break;
                }
            }
        }
    }
    public static void printStats() throws IOException {
        List<String> beforeDirs = getDirPathList();
        Map<Defect4jCaseInfo,MappingIdenticalCheckResult> identicalCheckResultMap = new LinkedHashMap<>();
//        int cc = 0;
        for (String beforeDir : beforeDirs) {
//            cc += 1;
//            if (cc > 10) break;
            String projectDir = Path.of(beforeDir).getParent().getFileName().toString();
//            if (!projectDir.equals("Closure")) continue;
            String bugID = Path.of(beforeDir).getFileName().toString();
            Defect4jCaseInfo defect4jCaseInfo = new Defect4jCaseInfo(projectDir, bugID);
            String afterDir = beforeDir.replace("before","after");
            Set<ASTDiff> diffSet = new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(beforeDir),Path.of(afterDir)).getDiffSet();
            boolean _SameWithGreedy = isAllIdentical(diffSet, GREEDY);
            boolean _SameWithSimple = isAllIdentical(diffSet, SIMPLE);
            boolean _SameWithAny = _SameWithGreedy || _SameWithSimple;
//            printThisCaseStatus(beforeDir,"RM-GTG",_SameWithGreedy);
//            printThisCaseStatus(beforeDir,"RM-GTS",_SameWithSimple);
            if (!_SameWithAny)
                printThisCaseStatus(beforeDir,"RM-ANY",_SameWithAny);
            identicalCheckResultMap.put(defect4jCaseInfo,
                    new MappingIdenticalCheckResult(_SameWithGreedy,
                            _SameWithSimple,
                            _SameWithAny));
        }
        Map<String, Triple<Integer,Integer,Integer>> result = groupBasedOnProject(identicalCheckResultMap);
        System.out.println(result);
//        objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File("MappingsComparison.json"),identicalCheckResultMap);

    }

    private static Map<String, Triple<Integer,Integer,Integer>> groupBasedOnProject(Map<Defect4jCaseInfo,MappingIdenticalCheckResult> input){
        Map<String, Triple<Integer,Integer,Integer>> result = new LinkedHashMap<>();
        for (Map.Entry<Defect4jCaseInfo, MappingIdenticalCheckResult> defect4jCaseInfoMappingIdenticalCheckResultEntry : input.entrySet()) {
            Defect4jCaseInfo defect4jCaseInfo = defect4jCaseInfoMappingIdenticalCheckResultEntry.getKey();
            MappingIdenticalCheckResult curr = defect4jCaseInfoMappingIdenticalCheckResultEntry.getValue();
            String pid = defect4jCaseInfo.project;
            result.putIfAbsent(pid,Triple.of(0,0,0));
            Triple<Integer, Integer, Integer> pre_val = result.get(pid);
            Triple<Integer, Integer, Integer> newValue;
            int same_with_gtg = pre_val.getLeft();
            int same_with_gts = pre_val.getMiddle();
            int same_with_any = pre_val.getRight();

            if (curr.isSame_RM_GTG)
                same_with_gtg += 1;
            if (curr.isSame_RM_GTS)
                same_with_gts += 1;
            if (curr.isSame_RM_ANY)
                same_with_any += 1;

            newValue = Triple.of(same_with_gtg,same_with_gts,same_with_any);
            result.put(pid,newValue);
        }
        return result;
    }
    public static void printThisCaseStatus(String beforeDir, String compInfo, boolean _allIdentical) {
        String bugID = Path.of(beforeDir).getFileName().toString();
        String projectDir = Path.of(beforeDir).getParent().getFileName().toString();

        String output;
        if (_allIdentical)
            output = "Same mappings ";
        else {
            output = "Different mappings ";
        }
        output += String.format("between %s for project: %s, bugID: %s", compInfo, projectDir,bugID);
        System.out.println(output);
    }

    public static boolean isAllIdentical(Set<ASTDiff> diffSet, CompositeMatchers.CompositeMatcher matcher) throws JsonProcessingException {
        boolean _allIdentical = true;
//        Long end = null;
//        Long start = null;
        for (ASTDiff astDiff : diffSet) {
//            start = System.currentTimeMillis();
            MappingStore match = matcher.match(astDiff.src.getRoot(), astDiff.dst.getRoot());
            EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
//            end = System.currentTimeMillis();
            String gtg_exported = MappingExportModel.exportString(match);
            String rm_exported = MappingExportModel.exportString(astDiff.getAllMappings());
            if (!gtg_exported.equals(rm_exported)) {
                _allIdentical = false;
                break;
            }
        }
//        return Pair.of(_allIdentical, end - start);
        return _allIdentical;
    }

    public static List<String> getDirPathList() throws IOException {
        List<String> beforeDirs = new ArrayList<>();
        Files.walk(Path.of(getDefect4jDir() + "before")).filter(
                path -> !path.toFile().isFile() && !path.endsWith("before")).forEach(path -> {
                    Path folderName = path.getFileName();
                    boolean _eligible = true;
                    try {
                        Integer.parseInt(String.valueOf(folderName));
                    } catch (NumberFormatException nfe) {
                        _eligible = false;
                    }
                    if (_eligible)
                        beforeDirs.add(path.toString());
        });
        beforeDirs.sort((o1, o2) -> {
            Path p1 = Path.of(o1);
            Path p2 = Path.of(o2);
            int result = p1.getParent().getFileName().toString().compareTo(p2.getParent().getFileName().toString());
            if (result != 0) return result;
            return Integer.compare(
                    Integer.parseInt(p1.getFileName().toString()),
                    Integer.parseInt(p2.getFileName().toString()));
        });
        return beforeDirs;
    }

    public static List<String> fileToLines(String filename) throws IOException {
        List<String> lines = new LinkedList<String>();
        String line = "";
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
}


