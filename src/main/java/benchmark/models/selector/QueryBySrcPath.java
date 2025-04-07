package benchmark.models.selector;

import org.refactoringminer.astDiff.models.ASTDiff;

public class QueryBySrcPath extends AbstractQueryByFunction {
    public QueryBySrcPath(String filename) {
        super(filename, ASTDiff::getSrcPath);
    }
}
