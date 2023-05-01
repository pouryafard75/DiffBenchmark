package benchmark.oracle.generators.changeAPI;

import benchmark.oracle.generators.APIChangerException;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import shaded.com.github.gumtreediff.tree.ITree;
import shaded.org.eclipse.jdt.core.dom.ASTNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-04-17 8:01 p.m. */
public class Utils {
    public static Set<Mapping> convert(Set<shaded.com.github.gumtreediff.matchers.Mapping> mappingSet, String srcContent, String dstContent) throws  APIChangerException, IOException, RuntimeException {
        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContent).getRoot(); // instantiates and applies the JDT generator
        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContent).getRoot(); // instantiates and applies the JDT generator
        Set<com.github.gumtreediff.matchers.Mapping> output = new LinkedHashSet<>();
        boolean _fail = false;
        for (shaded.com.github.gumtreediff.matchers.Mapping mapping : mappingSet) {
            try {
                Tree first = convertITreeToTree(srcTree, mapping.first);
                Tree second = convertITreeToTree(dstTree, mapping.second);
                if (first == null || second == null) {
                    System.out.println("Missed");
                    throw new RuntimeException("Missed");
//                continue;
                }
                output.add(new com.github.gumtreediff.matchers.Mapping(first, second));
            }
            catch (APIChangerException apiChangerException){
                _fail = true;
            }
        }
        if (_fail)
            throw new APIChangerException("Failed for at least one");
        return output;
    }
    private static Tree convertITreeToTree(Tree inpTree, ITree input) throws APIChangerException{
//        Tree result = TreeUtilFunctions.getTreeBetweenPositions(inpTree,input.getPos(),input.getEndPos());
        List<Tree> ret = getTreesExactPosition(inpTree,input.getPos(),input.getEndPos());
        Tree result = null;
        if (ret.size() == 0) {
            System.out.println("Failed");
            throw new APIChangerException("WTF");
        }
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
                throw new APIChangerException("OOPS");
        }
        else{
            result = ret.get(0);
        }
        return result;
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
