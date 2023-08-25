package gui.webdiff;

import com.github.gumtreediff.utils.Pair;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.util.*;

public class DirComparator {
    private final List<ASTDiff> diffs;
    private final ProjectASTDiff projectASTDiff;
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
                projectASTDiff.getFileContentsBefore().get(diffs.get(id).getSrcPath()),
                projectASTDiff.getFileContentsAfter().get(diffs.get(id).getDstPath())
        );
    }

    public DirComparator(ProjectASTDiff projectASTDiff)
    {
        this.projectASTDiff = projectASTDiff;
        this.diffs = new ArrayList<>(projectASTDiff.getDiffSet());
        modifiedFilesName = new LinkedHashMap<>();

        compare();
    }

    private void compare() {
        Set<String> beforeFiles = projectASTDiff.getFileContentsBefore().keySet();
        Set<String> afterFiles = projectASTDiff.getFileContentsAfter().keySet();

        removedFilesName = new HashSet<>(beforeFiles);
        addedFilesName = new HashSet<>(afterFiles);

        for (ASTDiff diff : diffs) {
            modifiedFilesName.put(diff.getSrcPath(),diff.getDstPath());
            removedFilesName.remove(diff.getSrcPath());
            addedFilesName.remove(diff.getDstPath());
        }
        Set<String> removedBackup = new HashSet<>(removedFilesName);
        removedFilesName.removeAll(addedFilesName);
        addedFilesName.removeAll(removedBackup);
    }
    public ASTDiff getASTDiff(int id) {
        return diffs.get(id);
    }
}
