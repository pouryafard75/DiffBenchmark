package benchmark.metrics.models;

import benchmark.utils.AdditionalASTConstants;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.List;

import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.isStatement;

/* Created by pourya on 2023-04-16 5:07 a.m. */
public class DiffIgnore {
    private final Tree src;
    private final Tree dst;
    private final ExtendedMultiMappingStore multiMappings;
    public int _ignoredElements;
    public int _ignoredMappings;

    public int getNumberOfIgnoredElements() {
        return _ignoredElements;
    }

    public int getNumberOfIgnoredMappings() {
        return _ignoredMappings;
    }

    public DiffIgnore(Tree src, Tree dst, ExtendedMultiMappingStore multiMappings) {

        this.src = src;
        this.dst = dst;
        this.multiMappings = multiMappings;
    }
    public void run(){
        for (Mapping mapping : multiMappings) {
            if (mapping.first.getType().name.equals(Constants.METHOD_DECLARATION))
            {
                Tree srcFinalParent = TreeUtilFunctions.getParentUntilType(mapping.first, AdditionalASTConstants.COMPILATION_UNIT);
                Tree dstFinalParent = TreeUtilFunctions.getParentUntilType(mapping.second, AdditionalASTConstants.COMPILATION_UNIT);
                if (!srcFinalParent.equals(src) || !dstFinalParent.equals(dst)) continue;
                if (mapping.first.isIsomorphicTo(mapping.second))
                {
                    Tree srcEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.first.getParent(), Constants.METHOD_DECLARATION);
                    Tree dstEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.second.getParent(), Constants.METHOD_DECLARATION);
                    if (srcEnclosingMethod != null && dstEnclosingMethod != null)
                    {
                        if (srcEnclosingMethod.isIsomorphicTo(dstEnclosingMethod)) {
                            _ignoredElements += 1;
                            _ignoredMappings += getNumberOfMethodSignatureIgnoresForIsomorphic(mapping);
                            continue;
                        }
                    }
                    _ignoredElements += 1;
                    List<Tree> descendants = mapping.first.getDescendants();
                    for (Tree descendant : descendants) {
                        if (isStatement(descendant.getType().name))
                            _ignoredMappings += 1;
                    }
                    _ignoredMappings += getNumberOfMethodSignatureIgnoresForIsomorphic(mapping);
                }
            }
        }
    }
    private static int getNumberOfMethodSignatureIgnoresForIsomorphic(Mapping mapping) {
        int result = 0;
        for (Tree child : mapping.first.getChildren()) {
            if (child.getType().name.equals(Constants.BLOCK)) continue;
            if (child.getType().name.equals(AdditionalASTConstants.JAVA_DOC)) continue;
            result += 1;
        }
        return result;
    }
}
