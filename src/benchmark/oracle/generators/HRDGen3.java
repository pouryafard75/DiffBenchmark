package benchmark.oracle.generators;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.TreeAddition;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.actions.model.MoveIn;
import org.refactoringminer.astDiff.actions.model.MoveOut;
import org.refactoringminer.astDiff.matchers.Constants;

import static benchmark.oracle.generators.GeneratorUtils.*;
import static benchmark.oracle.generators.GeneratorUtils.generateFieldSignature;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.isStatement;

/* Created by pourya on 2023-09-15 4:26 p.m. */
public class HRDGen3 extends HumanReadableDiffGenerator {
    public HRDGen3(ProjectASTDiff projectASTDiff, ASTDiff generated, CaseInfo info, Configuration configuration) {
        super(projectASTDiff, generated, info, configuration);
    }

    @Override
    void make() {
        this.populate();
    }


    private void populate() {
        String currSrc = "";
        String currDst = "";
        Tree src = getAstDiff().src.getRoot();
        Tree dst = getAstDiff().dst.getRoot();
        NecessaryMappings target = null;
        boolean _inter = false;
        for (Mapping mapping : getAstDiff().getAllMappings()) {
            if (isPartOfJavadoc(mapping)) continue;
            if (isBetweenDifferentTypes(mapping)) throw new RuntimeException();
            String firstType = mapping.first.getType().name; //Second type definitely has the same type due to the previous check
            if (isInterFileMapping(mapping,src,dst))
            {
                _inter = true;
                boolean _found = false;
                if (isProgramElement(firstType)|| isStatement(firstType)) {
                    for (Action e : getAstDiff().editScript) {
                        if (e instanceof MoveOut || e instanceof MoveIn) {
                            if (e.getNode().toTreeString().equals(mapping.first.toTreeString()) ||
                                    e.getNode().toTreeString().equals(mapping.second.toTreeString()) ||
                                    ((TreeAddition) e).getParent().toTreeString().equals(mapping.first.toTreeString()) ||
                                    ((TreeAddition) e).getParent().toTreeString().equals(mapping.second.toTreeString()))
                            {
                                String key = e.toString();
                                getResult().interFileMappings.putIfAbsent(key, new NecessaryMappings());
                                target = getResult().interFileMappings.get(key);
                                if (e instanceof MoveIn)
                                {
                                    currSrc = ((MoveIn) e).getSrcFile();
                                    currDst = getAstDiff().getDstPath();
                                }
                                else {
                                    currSrc = getAstDiff().getSrcPath();
                                    currDst = ((MoveOut) e).getDstFile();
                                }
                                _found = true;
                                break;
                            }
                        }
                    }
                    if (!_found)
                    {
                        continue;
                    }
                }
                else {
                    //Note: We are skipping anything except ProgramElementMappings and statement for inter-files
                    continue;
                }
            }
            else
            {
                target = getResult().intraFileMappings;
                _inter = false;
                currSrc = getAstDiff().getSrcPath();
                currDst = getAstDiff().getDstPath();
            }
            switch (firstType)
            {
                case Constants.TYPE_DECLARATION:
                    addAccordingly(
                            new AbstractMapping(mapping,
                                    generateClassSignature(mapping.first,getSrcContent(currSrc)),
                                    generateClassSignature(mapping.second,getDstContent(currDst))),
                            target);
                    if (!_inter)
                        handleTypeDeclaration(mapping,target,currSrc,currDst);
                    break;
                case Constants.METHOD_DECLARATION:
                    addAccordingly(
                            new AbstractMapping(
                                    mapping,
                                    generateMethodSignature(mapping.first,getSrcContent(currSrc)),
                                    generateMethodSignature(mapping.second,getDstContent(currDst))),
                            target);
                    if (!_inter)
                        handleMethodDeclaration(mapping,target,currSrc,currDst);
                    break;
                case Constants.FIELD_DECLARATION:
                    addAccordingly(
                            new AbstractMapping(
                                    mapping,generateFieldSignature(
                                    mapping.first,getSrcContent(currSrc)),
                                    generateFieldSignature(mapping.second,getDstContent(currDst))),
                            target);
                    if (!_inter)
                        handleFieldDeclaration(mapping,target,currSrc,currDst);
                    break;
            }
            handleStatement(mapping, target,currSrc,currDst);
            if (!_inter) {
                handleMove(mapping, target, currSrc, currDst);
                handleUpdate(mapping, target, currSrc, currDst);
            }
        }
    }
    void handleUpdate(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        /*Update case*/
        if (!mapping.first.getLabel().equals(mapping.second.getLabel()))
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
    }
    void handleMove(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        if (mapping.first.getParent() == null || mapping.second.getParent() == null) return;
        Tree firstParent = mapping.first.getParent();
        Tree secondParent = mapping.second.getParent();
        if (getAstDiff().getAllMappings().getDsts(firstParent) == null)
            /*Source's parent is not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
        else if (!getAstDiff().getAllMappings().getDsts(firstParent).contains(secondParent))
            /*Parents are not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
        //Todo: Check if this is necessary
//        else if (mapping.first.positionInParent() != mapping.second.positionInParent())
//            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
    }
}
