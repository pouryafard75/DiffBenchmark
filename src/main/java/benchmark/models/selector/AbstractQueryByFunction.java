package benchmark.models.selector;

import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.function.Function;

public abstract class AbstractQueryByFunction implements DiffSelector {
    private final String filename;
    private final Function<ASTDiff, String> function;
    protected AbstractQueryByFunction(String filename, Function<ASTDiff, String> function) {
        this.filename = filename;
        this.function = function;
    }

    @Override
    public ASTDiff apply(ProjectASTDiff projectASTDiff){
        return projectASTDiff.getDiffSet().stream().filter(
                diff -> function.apply(diff).equals(filename)).findFirst().get();
    }
}
