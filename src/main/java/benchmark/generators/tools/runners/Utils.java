package benchmark.generators.tools.runners;

import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.gui.web.BenchmarkWebDiff;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.TypeSet;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import shaded.com.github.gumtreediff.tree.ITree;
import shaded.org.eclipse.jdt.core.dom.ASTNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

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

    public static ASTDiff makeASTDiff(ASTDiff input, com.github.gumtreediff.tree.Tree src, com.github.gumtreediff.tree.Tree dst, ExtendedMultiMappingStore mappings) {
        TreeContext srcContext = new TreeContext();
        srcContext.setRoot(src);
        TreeContext dstContext = new TreeContext();
        dstContext.setRoot(dst);
        return new ASTDiff(input.getSrcPath(), input.getDstPath(), srcContext, dstContext, mappings);
    }

    public static void writeAll(BenchmarkWebDiff benchmarkWebDiff) throws IOException {
        for (Map.Entry<IASTDiffTool, Set<ASTDiff>> iastDiffToolSetEntry : benchmarkWebDiff.diffs.entrySet()) {
            Set<ASTDiff> diffs = iastDiffToolSetEntry.getValue();
            int counter = 0;
            for (ASTDiff diff : diffs) {
                counter ++;
                FileUtils.writeStringToFile(new File(iastDiffToolSetEntry.getKey() + "_" + counter + "_src.txt"),
                        removeOffsets(diff.src.getRoot().toTreeString()));
                FileUtils.writeStringToFile(new File(iastDiffToolSetEntry.getKey() + "_" + counter + "_dst.txt"),
                        removeOffsets(diff.dst.getRoot().toTreeString()));
                FileUtils.writeStringToFile(new File(iastDiffToolSetEntry.getKey() + "_" + counter + "_diff.txt"),
                        mappingsToString(diff));
            }
        }
    }

    private static String removeOffsets(String treeString) {
        return treeString.replaceAll("\\[\\d+,\\s*\\d+]", "");

    }

    public static String mappingsToString(ASTDiff next) {
        //Sort based on the offsets
        List<Mapping> all = new ArrayList<>();
        for (Mapping allMapping : next.getAllMappings()) {
            all.add(allMapping);
        }
        Comparator<Mapping> mappingComparator;
        mappingComparator = Comparator.comparingInt(o -> o.first.getPos());
        mappingComparator = mappingComparator.thenComparingInt(o -> o.first.getEndPos());
        all.sort(mappingComparator);
        StringBuilder sb = new StringBuilder();
        for (Mapping mapping : all) {
            sb.append(mapping.toString());
            sb.append("\n");
        }
        return sb.toString();
    }

}
