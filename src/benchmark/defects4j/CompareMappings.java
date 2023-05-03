package benchmark.defects4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static benchmark.utils.PathResolver.*;

/* Created by pourya on 2023-05-02 7:42 p.m. */
public class CompareMappings {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(CompareMappings.class);
    private final static CompositeMatchers.CompositeMatcher matcher;

    static {
//        matcher = new CompositeMatchers.ClassicGumtree();
        matcher = new CompositeMatchers.SimpleGumtree();
    }

    public static void main(String[] args) throws IOException {
        List<String> beforeDirs = new ArrayList<>();
        getDirPathList(beforeDirs);

        for (String beforeDir : beforeDirs) {
            String afterDir = beforeDir.replace("before","after");
            Set<ASTDiff> diffSet = new GitHistoryRefactoringMinerImpl().diffAtDirectories(Path.of(beforeDir),Path.of(afterDir));
            boolean _allIdentical = isAllIdentical(diffSet);
            String bugID = Path.of(beforeDir).getFileName().toString();
            String projectDir = Path.of(beforeDir).getParent().getFileName().toString();
            if (_allIdentical)
                logger.info(String.format("Same mappings for project: %s, bugID: %s",projectDir,bugID));
            else {
                logger.info(String.format("Different mappings for project: %s, bugID: %s",projectDir,bugID));
            }
        }
    }

    private static boolean isAllIdentical(Set<ASTDiff> diffSet) throws JsonProcessingException {
        boolean _allIdentical = true;
        for (ASTDiff astDiff : diffSet) {
            MappingStore match = matcher.match(astDiff.src.getRoot(), astDiff.dst.getRoot());
            String gtg_exported = MappingExportModel.exportString(match);
            String rm_exported = MappingExportModel.exportString(astDiff.getAllMappings());
            if (!gtg_exported.equals(rm_exported))
            {
                _allIdentical = false;
                break;
            }
        }
        return _allIdentical;
    }

    private static void getDirPathList(List<String> beforeDirs) throws IOException {
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
        beforeDirs.sort(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                Path p1 =Path.of(o1);
                Path p2 = Path.of(o2);
                int result = p1.getParent().getFileName().toString().compareTo(p2.getParent().getFileName().toString());
                if (result != 0) return result;
                return Integer.compare(
                        Integer.parseInt(p1.getFileName().toString()),
                        Integer.parseInt(p2.getFileName().toString()));
            }
        });
    }
}
