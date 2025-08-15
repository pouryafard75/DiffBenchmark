package diffQ;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.List;

public class Utils {
    public static ProjectASTDiff makeToolsProjectASTDiff(IBenchmarkCase iBenchmarkCase, ProjectASTDiff ref, ASTDiffToolEnum tool) throws Exception {
        ProjectASTDiff projectASTDiff = new ProjectASTDiff(ref.getFileContentsBefore(), ref.getFileContentsAfter());
        for (ASTDiff astDiff : ref.getDiffSet()) {
            ASTDiff diff = tool.diff(iBenchmarkCase, (x) -> astDiff);
            projectASTDiff.addASTDiff(diff);
        }
        projectASTDiff.setMetaInfo(ref.getMetaInfo());
        projectASTDiff.setRefactorings(List.of());
        return projectASTDiff;
    }
}
