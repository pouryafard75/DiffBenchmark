package benchmark.generators.tools.models;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.utils.Experiments.IQuerySelector;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.Map;

/* Created by pourya on 2024-05-02*/
public abstract class ASTDiffProviderFromProjectASTDiff extends BaseASTDiffProvider {
    protected final ProjectASTDiff projectASTDiff;
    protected final Map<String, TreeContext> ptc;
    protected final Map<String, TreeContext> ctc;
    protected final IBenchmarkCase benchmarkCase;
    protected final IQuerySelector querySelector;

    public ASTDiffProviderFromProjectASTDiff(IBenchmarkCase benchmarkCase, IQuerySelector querySelector) {
        super(querySelector.apply(benchmarkCase.getProjectASTDiff()));
        this.projectASTDiff = benchmarkCase.getProjectASTDiff();
        this.ptc = projectASTDiff.getParentContextMap();
        this.ctc = projectASTDiff.getChildContextMap();
        this.benchmarkCase = benchmarkCase;
        this.querySelector = querySelector;
    }
}
