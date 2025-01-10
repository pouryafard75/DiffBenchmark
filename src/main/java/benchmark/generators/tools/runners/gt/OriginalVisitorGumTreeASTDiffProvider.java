package benchmark.generators.tools.runners.gt;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.io.IOException;

/* Created by pourya on 2024-11-26*/
public class OriginalVisitorGumTreeASTDiffProvider extends BaseGumTreeASTDiffProvider {
    private final String dstContent;
    private final String srcContent;
    private final JdtTreeGenerator treeGen = new JdtTreeGenerator();

    public OriginalVisitorGumTreeASTDiffProvider(Matcher matcher, ASTDiff input, String srcContent, String dstContent) {
        super(matcher, input);
        this.srcContent = srcContent;
        this.dstContent = dstContent;
        setRoots(srcContent, dstContent);
    }

    public OriginalVisitorGumTreeASTDiffProvider(Matcher matcher, IBenchmarkCase benchmarkCase, DiffSelector query) {
        super(matcher, query.apply(benchmarkCase.getProjectASTDiff()));
        ProjectASTDiff projectASTDiff = benchmarkCase.getProjectASTDiff();
        this.srcContent = projectASTDiff.getFileContentsBefore().get(input.getSrcPath());
        this.dstContent = projectASTDiff.getFileContentsAfter().get(input.getDstPath());
        setRoots(srcContent, dstContent);
    }


    private void setRoots(String srcContent, String dstContent) {
        setSrcRoot(getTree(srcContent));
        setDstRoot(getTree(dstContent));
    }

    protected Tree getTree(String content) {
        try {
            return treeGen.generateFrom().string(content).getRoot();
        } catch (IOException e) {
            System.err.println(e);
            throw new RuntimeException(e);
        }
    }
}
