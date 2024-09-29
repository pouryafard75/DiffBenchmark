package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.ExperimentConfiguration;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import shaded.org.eclipse.jdt.core.dom.ASTNode;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Set;

/* Created by pourya on 2024-09-06*/
public class IAST extends AbstractASTDiffProviderFromMappingSet {

    public IAST(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info, ExperimentConfiguration conf) {
        super(projectASTDiff, input, info, conf);
    }

    @Override
    protected Set<Mapping> getMappings() throws IOException {
        cs.model.algorithm.iASTMapper m = new cs.model.algorithm.iASTMapper(srcContents, dstContents);
        m.buildMappingsOuterLoop();
        for (iast.com.github.gumtreediff.matchers.Mapping treeMapping : m.getTreeMappings()) {
            //TODO:
        }
        return null; //TODO
    }

    // Helper method to invoke methods using reflection
    private Object callMethod(Object obj, String methodName) throws Exception {
        Method method = obj.getClass().getMethod(methodName);  // Get method by name
        return method.invoke(obj);  // Invoke the method on the object
    }

    private static String getReplacedType(int type) throws Exception {
        String replace = ASTNode.nodeClassForType(type).getName().replace("shaded.org.eclipse.jdt.core.dom.", "");
        if (replace.equals(""))
            throw new Exception("Cannot find AST-Type");
        return replace;
    }

}
