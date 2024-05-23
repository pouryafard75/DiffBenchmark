package benchmark.generators.tools.models;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.Map;

/* Created by pourya on 2024-05-02*/
public abstract class ASTDiffProviderFromProjectASTDiff extends BaseASTDiffProvider {
    protected final ProjectASTDiff projectASTDiff;
    protected final Map<String, TreeContext> ptc;
    protected final Map<String, TreeContext> ctc;
    protected final CaseInfo info;
    protected final Configuration conf;

    protected ASTDiffProviderFromProjectASTDiff(ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo info, Configuration conf) {
        super(input);
        this.projectASTDiff = projectASTDiff;
        this.ptc = projectASTDiff.getParentContextMap();
        this.ctc = projectASTDiff.getChildContextMap();
        this.info = info;
        this.conf = conf;
    }
    protected ASTDiffProviderFromProjectASTDiff(ProjectASTDiff projectASTDiff, ASTDiff input) {
        this(projectASTDiff, input, null, null);
    }
}
