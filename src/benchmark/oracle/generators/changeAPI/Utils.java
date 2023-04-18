package benchmark.oracle.generators.changeAPI;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.tree.ITree;
import shaded.org.eclipse.jdt.core.dom.ASTNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-04-17 8:01 p.m. */
public class Utils {
    public static Set<Mapping> convert(Set<shaded.com.github.gumtreediff.matchers.Mapping> mappingSet, String srcContent, String dstContent) throws IOException {
        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContent).getRoot(); // instantiates and applies the JDT generator
        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContent).getRoot(); // instantiates and applies the JDT generator
        Set<com.github.gumtreediff.matchers.Mapping> output = new LinkedHashSet<>();
        for (shaded.com.github.gumtreediff.matchers.Mapping mapping : mappingSet) {
            Tree first  = convertITreeToTree(srcTree, mapping.first);
            Tree second  = convertITreeToTree(dstTree, mapping.second);
            if (first == null || second == null)
            {
                System.out.println("Missed");
                continue;
            }
            output.add(new com.github.gumtreediff.matchers.Mapping(first,second));
        }
        return output;
    }
    private static Tree convertITreeToTree(Tree inpTree, ITree input) {
//        Tree result = TreeUtilFunctions.getTreeBetweenPositions(inpTree,input.getPos(),input.getEndPos());
        List<Tree> ret = getTreesExactPosition(inpTree,input.getPos(),input.getEndPos());
        Tree result = null;
        if (ret.size() > 1)
        {
            String replaced = ASTNode.nodeClassForType(input.getType()).getName().replace("shaded.org.eclipse.jdt.core.dom.", "");
            for (Tree tree : ret) {
                if (tree.getType().name.equals(replaced))
                {
                    result = tree;
                    break;
                }
            }
            if (result == null)
                System.out.println();
        }
        else{
            result = ret.get(0);
        }
        return result;
    }
    public static Tree changeAPI(Tree target, ITree input) throws Exception {
        List<Tree> ret = getTreesExactPosition(target,input.getPos(),input.getEndPos());
        Tree result = null;
        if (ret.size() > 1)
        {
            String replaced = ASTNode.nodeClassForType(input.getType()).getName().replace("shaded.org.eclipse.jdt.core.dom.", "");
            for (Tree tree : ret) {
                if (tree.getType().name.equals(replaced))
                {
                    result = tree;
                    break;
                }
            }
            if (result == null)
                throw new Exception();
        }
        else{
            result = ret.get(0);
        }

        target.getTreesBetweenPositions(input.getPos(),input.getEndPos());
        DefaultTree clone = TreeUtilFunctions.makeDefaultTree(result);
        for (ITree child : input.getChildren()) {
            clone.addChild(changeAPI(target,child));
        }
        return clone;
    }
    public static Tree myMethod(Tree target, String content) throws Exception {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        ITree srcITree = gen.generateFromString(content).getRoot();
        Tree cloned = changeAPI(target, srcITree);
        System.out.println();
        return cloned;
    }
    public static List<Tree> getTreesExactPosition(Tree tree, int position, int endPosition) {
        List<Tree> ret = new ArrayList<>();
        for (Tree t: tree.preOrder()) {
            if (t.getPos() == position && t.getEndPos() == endPosition)
                ret.add(t);
        }
        return ret;
    }

}
