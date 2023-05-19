package benchmark.oracle.generators.changeAPI;

import benchmark.oracle.generators.APIChangerException;
import com.github.gumtreediff.actions.model.Delete;
import com.github.gumtreediff.actions.model.Insert;
import com.github.gumtreediff.actions.model.Move;
import com.github.gumtreediff.actions.model.Update;
import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TypeSet;
import shaded.com.github.gumtreediff.actions.model.Action;
import shaded.com.github.gumtreediff.tree.ITree;
import shaded.org.eclipse.jdt.core.dom.ASTNode;

import java.util.ArrayList;
import java.util.List;

/* Created by pourya on 2023-04-17 8:01 p.m. */
public class Utils {
//    public static Set<Mapping> convert(Set<shaded.com.github.gumtreediff.matchers.Mapping> mappingSet, String srcContent, String dstContent) throws  APIChangerException, IOException, RuntimeException {
//        Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContent).getRoot(); // instantiates and applies the JDT generator
//        Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContent).getRoot(); // instantiates and applies the JDT generator
//        Set<com.github.gumtreediff.matchers.Mapping> output = new LinkedHashSet<>();
//        boolean _fail = false;
//        for (shaded.com.github.gumtreediff.matchers.Mapping mapping : mappingSet) {
//            try {
//                Tree first = convertITreeToTree(srcTree, mapping.first);
//                Tree second = convertITreeToTree(dstTree, mapping.second);
//                if (first == null || second == null) {
//                    System.out.println("Missed");
//                    throw new RuntimeException("Missed");
////                continue;
//                }
//                output.add(new com.github.gumtreediff.matchers.Mapping(first, second));
//            }
//            catch (APIChangerException apiChangerException){
//                _fail = true;
//            }
//        }
//        if (_fail)
//            throw new APIChangerException("Failed for at least one");
//        return output;
//    }
    private static Tree convertITreeToTree(Tree inpTree, ITree input) throws Exception {
//        Tree result = TreeUtilFunctions.getTreeBetweenPositions(inpTree,input.getPos(),input.getEndPos());
        List<Tree> ret = getTreesExactPosition(inpTree,input.getPos(),input.getEndPos());
        Tree result = null;
        if (ret.size() == 0) {
            System.out.println("Failed");
            throw new APIChangerException("WTF");
        }
        if (ret.size() > 1)
        {
            String replaced = getReplacedType(input);
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
    public static com.github.gumtreediff.actions.model.Action mirrorAction(Action iAction, Tree src, Tree dst) throws Exception {
        com.github.gumtreediff.actions.model.Action action;
        if (iAction instanceof shaded.com.github.gumtreediff.actions.model.Insert)
        {
            shaded.com.github.gumtreediff.actions.model.Insert insert = (shaded.com.github.gumtreediff.actions.model.Insert) iAction;
            action = new Insert(findMirror(insert.getNode(),dst),findMirror(insert.getParent(),dst),insert.getPosition());
        }
        else if (iAction instanceof shaded.com.github.gumtreediff.actions.model.Delete)
        {
            shaded.com.github.gumtreediff.actions.model.Delete delete = (shaded.com.github.gumtreediff.actions.model.Delete) iAction;
            action = new Delete(findMirror(delete.getNode(),src));
        }
        else if (iAction instanceof shaded.com.github.gumtreediff.actions.model.Move)
        {
            shaded.com.github.gumtreediff.actions.model.Move move = (shaded.com.github.gumtreediff.actions.model.Move) iAction;
            action = new Move(findMirror(move.getNode(),src),findMirror(move.getParent(),dst),move.getPosition());
        }
        else if (iAction instanceof shaded.com.github.gumtreediff.actions.model.Update)
        {
            shaded.com.github.gumtreediff.actions.model.Update update = (shaded.com.github.gumtreediff.actions.model.Update) iAction;
            action = new Update(findMirror(update.getNode(),src),update.getValue());
        }
        else
        {
            throw new Exception("Bug");
        }
        return action;
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
