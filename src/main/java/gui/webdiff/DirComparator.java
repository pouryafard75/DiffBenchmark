package gui.webdiff;

import com.github.gumtreediff.utils.Pair;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.*;

public class DirComparator {
    private Map<String, String> fileContentsBeforeMap;
    private Map<String, String> fileContentsAfterMap;
    private final List<ASTDiff> diffs;
    private Set<String> removedFilesName;
    private Set<String> addedFilesName;
    private Map<String,String> modifiedFilesName;

    public Set<String> getRemovedFilesName() {
        return removedFilesName;
    }

    public Set<String> getAddedFilesName() {
        return addedFilesName;
    }

    public Map<String,String> getModifiedFilesName() {
        return modifiedFilesName;
    }
    public Pair<String,String> getFileContentsPair(int id)
    {
        return new Pair<>(
                diffs.get(id).getSrcContents(),
                diffs.get(id).getDstContents()
        );
    }

    public DirComparator(Set<ASTDiff> diffs)
    {
        this.diffs = new ArrayList<>(diffs);
        fileContentsBeforeMap = new LinkedHashMap<>();
        fileContentsAfterMap = new LinkedHashMap<>();
        modifiedFilesName = new LinkedHashMap<>();

        populateNameToContentMap();
        compare();
    }
    private void populateNameToContentMap()
    {
        for (ASTDiff diff : diffs) {
            this.fileContentsBeforeMap.put(
                    diff.getSrcPath(),diff.getSrcContents()
            );
            this.fileContentsAfterMap.put(
                    diff.getDstPath(),diff.getDstContents()
            );
        }
    }
    private void compare() {
        Set<String> beforeFiles = fileContentsBeforeMap.keySet();
        Set<String> afterFiles = fileContentsAfterMap.keySet();

        removedFilesName = new HashSet<>(beforeFiles);
        addedFilesName = new HashSet<>(afterFiles);

        for (ASTDiff diff : diffs) {
            modifiedFilesName.put(diff.getSrcPath(),diff.getDstPath());
            removedFilesName.remove(diff.getSrcPath());
            addedFilesName.remove(diff.getDstPath());
        }
    }
    public ASTDiff getASTDiff(int id) {
        return diffs.get(id);
    }
}
