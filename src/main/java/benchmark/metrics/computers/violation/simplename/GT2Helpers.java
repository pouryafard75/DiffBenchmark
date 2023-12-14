package benchmark.metrics.computers.violation.simplename;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* Created by pourya on 2023-12-13 9:36 p.m. */
public class GT2Helpers {
    private final static Logger logger = LoggerFactory.getLogger(GT2Helpers.class);
    public static String findGT2TranslatedParentTypeForMethodInvocations(Tree input, Tree threePointZeroTree) {
        if (input.getParent() == null) throw new RuntimeException("Parent is null");
//        if (!input.getParent().getType().name.equals(Constants.METHOD_INVOCATION)) throw new RuntimeException("Parent is not method invocation");
        Tree eqv = TreeUtilFunctions.getTreeBetweenPositions(threePointZeroTree, input.getPos(), input.getEndPos(), input.getType().name);
        if (eqv == null) {
            logger.error("eqv is null for " + input.getType().name + " " + input.getLabel() + " " + input.getPos() + " " + input.getEndPos());
            return null;
        }

        return eqv.getParent().getType().name;
    }

}
