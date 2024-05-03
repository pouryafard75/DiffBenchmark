package benchmark.generators.tools.runners;

import benchmark.generators.tools.models.ASTDiffProviderFromProjectASTDiff;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import com.github.gumtreediff.tree.TypeSet;
import iast.com.github.gumtreediff.matchers.Mapping;
import iast.com.github.gumtreediff.tree.ITree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import static benchmark.generators.tools.runners.Utils.getTreesExactPosition;
import static benchmark.generators.tools.runners.shaded.AbstractASTDiffProviderFromIncompatibleTree.diffToASTDiffWithActions;

/* Created by pourya on 2024-01-10*/
public class IASTMapper extends ASTDiffProviderFromProjectASTDiff {
    private final String srcContents;
    private final String dstContents;

    private final static Logger logger = LoggerFactory.getLogger(IASTMapper.class);


    public IASTMapper(ProjectASTDiff projectASTDiff, ASTDiff input) {
        super(projectASTDiff, input);
        this.srcContents = projectASTDiff.getFileContentsBefore().get(input.getSrcPath());
        this.dstContents = projectASTDiff.getFileContentsAfter().get(input.getDstPath());
    }


    public ASTDiff makeASTDiff() throws Exception {
        return diffToASTDiffWithActions(diff(), input.getSrcPath(), input.getDstPath());
    }
    public Diff diff() throws Exception {
        cs.model.algorithm.iASTMapper m = new cs.model.algorithm.iASTMapper(srcContents, dstContents);
        m.buildMappingsOuterLoop();
        Tree srcMirror = mirrorTree(m.getSrc());
        Tree dstMirror = mirrorTree(m.getDst());

        TreeContext srcTC = new TreeContext();
        srcTC.setRoot(srcMirror);
        TreeContext dstTC = new TreeContext();
        dstTC.setRoot(dstMirror);
        com.github.gumtreediff.matchers.MappingStore mappingStore = new MappingStore(srcMirror,dstMirror);
        EditScript editScript = new EditScript();
        try {
            for (Mapping mapping : m.getTreeMappings()) {
                Tree firstMirror = findMirror(mapping.first, srcMirror);
                Tree secondMirror = findMirror(mapping.second, dstMirror);
                assert firstMirror != null;
                assert secondMirror != null;
                if (!firstMirror.getType().name.equals(secondMirror.getType().name)) {
                    logger.info("Types are not equal: " +
                            firstMirror + " " + secondMirror);
//                    continue;
                }
                mappingStore.addMapping(firstMirror, secondMirror);
            }
            editScript = new SimplifiedChawatheScriptGenerator().computeActions(mappingStore);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Must check");
        }
        return new Diff(srcTC, dstTC, mappingStore, editScript);
    }
    private static Tree findMirror(ITree iTree, Tree fullTree) throws Exception {
        List<Tree> treesBetweenPositions = getTreesExactPosition(fullTree, iTree.getPos(), iTree.getEndPos());
        for (Tree treeBetweenPosition : treesBetweenPositions) {
            if (treeBetweenPosition.getType().name.equals(iTree.getType().name))
                return treeBetweenPosition;
        }
        return null;
    }
    private static Tree mirrorTree(ITree iTree) throws Exception {
        String replacedType = iTree.getType().name;
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


    private static Tree whichTree(cs.model.algorithm.iASTMapper m, Tree srcMirror, Tree dstMirror, ITree input) throws Exception {
        ITree tempParent = input;
        while (tempParent.getParent() != null)
            tempParent = tempParent.getParent();
        Tree decision;
        if (tempParent == m.getSrc())
            decision = srcMirror;
        else if (tempParent == m.getDst())
            decision = dstMirror;
        else
            throw new Exception("Must check");
        return decision;
    }
}
