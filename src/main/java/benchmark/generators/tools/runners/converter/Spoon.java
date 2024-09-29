package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProviderFromProjectASTDiff;
import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.TypeSet;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import shadedspoon.com.github.gumtreediff.matchers.Mapping;
import shadedspoon.com.github.gumtreediff.tree.Tree;
import shadedspoon.gumtree.spoon.AstComparator;
import shadedspoon.gumtree.spoon.diff.DiffImpl;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.support.compiler.VirtualFile;

import java.util.List;

import static benchmark.generators.tools.runners.Utils.getTreesExactPosition;

/* Created by pourya on 2024-09-09*/
public class Spoon extends ASTDiffProviderFromProjectASTDiff {
    public Spoon(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info) {
        super(projectASTDiff, input, info);
    }

    @Override
    public ASTDiff makeASTDiff() throws Exception {
        CtElement leftCt = getCtPackageFromContent(projectASTDiff.getFileContentsBefore().get(input.getSrcPath()));
        CtElement rightCt = getCtPackageFromContent(projectASTDiff.getFileContentsAfter().get(input.getDstPath()));
        shadedspoon.gumtree.spoon.builder.SpoonGumTreeBuilder scanner = new shadedspoon.gumtree.spoon.builder.SpoonGumTreeBuilder();
        DiffImpl diff = new DiffImpl(scanner.getTreeContext(), scanner.getTree(leftCt), scanner.getTree(rightCt));
        com.github.gumtreediff.tree.Tree src = unshaded(diff.getMappingsComp().src);
        com.github.gumtreediff.tree.Tree dst = unshaded(diff.getMappingsComp().dst);

        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(src, dst);
        populate(mappings, diff, src, dst);
        TreeContext srcContext = new TreeContext();
        srcContext.setRoot(src);
        TreeContext dstContext = new TreeContext();
        dstContext.setRoot(dst);
        ASTDiff astDiff = new ASTDiff(input.getSrcPath(), input.getDstPath(), srcContext, dstContext, mappings);
        astDiff.computeVanillaEditScript();
        return astDiff;
    }

    private static void populate(ExtendedMultiMappingStore mappings, DiffImpl diff, com.github.gumtreediff.tree.Tree src, com.github.gumtreediff.tree.Tree dst) {
        for (Mapping mapping : diff.getMappingsComp()) {
            mappings.addMapping(findMirror(mapping.first,src), findMirror(mapping.second, dst));
        }
    }
    private static com.github.gumtreediff.tree.Tree findMirror(Tree iTree, com.github.gumtreediff.tree.Tree fullTree) {
        List<com.github.gumtreediff.tree.Tree> treesBetweenPositions = getTreesExactPosition(fullTree, iTree.getPos(), iTree.getEndPos());
        for (com.github.gumtreediff.tree.Tree treeBetweenPosition : treesBetweenPositions) {
            if (treeBetweenPosition.getType().name.equals(iTree.getType().name))
                return treeBetweenPosition;
        }
        return null;
    }

    private com.github.gumtreediff.tree.Tree unshaded(Tree t) {
        DefaultTree curr = new DefaultTree(TypeSet.type(t.getType().name));
        curr.setLabel(t.getLabel());
        curr.setPos(t.getPos());
        curr.setLength(t.getLength());
        for (Tree child : t.getChildren()) {
            com.github.gumtreediff.tree.Tree childMirror = unshaded(child);
            childMirror.setParent(curr);
            curr.addChild(childMirror);
        }
        return curr;
    }


    private static CtPackage getCtPackageFromContent(String content) {
        VirtualFile resource = new VirtualFile(content, getFilename(content));
        return new AstComparator().getCtPackage(resource);
    }
    private static String getFilename(String content) {
        return "test"+Math.abs(content.hashCode()) + ".java";
    }


}
