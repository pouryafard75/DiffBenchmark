package benchmark.utils.Experiments;

import org.refactoringminer.astDiff.models.ASTDiff;

public class QueryByDstPath extends AbstractQueryByFunction {
    public QueryByDstPath(String filename) {
        super(filename, ASTDiff::getDstPath);
    }
}