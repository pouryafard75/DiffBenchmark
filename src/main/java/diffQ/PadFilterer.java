package diffQ;

import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.Map;

public interface PadFilterer {
    Map<String, ProjectASTDiff> filter(Map<String, ProjectASTDiff> pads);
}
