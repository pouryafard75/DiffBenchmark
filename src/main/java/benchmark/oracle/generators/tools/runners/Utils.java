package benchmark.oracle.generators.tools.runners;

import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TypeSet;
import shaded.com.github.gumtreediff.tree.ITree;
import shaded.org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.List;

/* Created by pourya on 2023-04-17 8:01 p.m. */
public class Utils {
    public static List<Tree> getTreesExactPosition(Tree tree, int position, int endPosition) {
        List<Tree> ret = new ArrayList<>();
        for (Tree t: tree.preOrder()) {
            if (t.getPos() == position && t.getEndPos() == endPosition)
                ret.add(t);
        }
        return ret;
    }
    public static Tree findMirror(ITree iTree, Tree fullTree) throws Exception {
        List<Tree> treesBetweenPositions = getTreesExactPosition(fullTree, iTree.getPos(), iTree.getEndPos());
        for (Tree treeBetweenPosition : treesBetweenPositions) {
            if (treeBetweenPosition.getType().name.equals(getReplacedType(iTree)))
                return treeBetweenPosition;
        }
        return null;
    }
    public static Tree mirrorTree(ITree iTree) throws Exception {
        String replacedType = getReplacedType(iTree);
        DefaultTree curr = new DefaultTree(TypeSet.type(replacedType));
        curr.setLabel(iTree.getLabel());
        curr.setPos(iTree.getPos());
        curr.setLength(iTree.getLength());
        for (ITree iChild : iTree.getChildren()) {
            Tree childMirror = mirrorTree(iChild);
            childMirror.setParent(curr);
            curr.addChild(childMirror);

        }
        return curr;
    }

    private static String getReplacedType(ITree iTree) throws Exception {
        String replace = ASTNode.nodeClassForType(iTree.getType()).getName().replace("shaded.org.eclipse.jdt.core.dom.", "");
        if (replace.equals(""))
            throw new Exception("Cannot find AST-Type");
        return replace;
    }
}
