package benchmark.oracle.generators.tools.runners;

import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/* Created by pourya on 2024-02-20*/
public abstract class ASTDiffConvertor {
    protected final String srcPath;
    protected final ProjectASTDiff projectASTDiff;
    protected ASTDiff rm_astDiff;
    protected Map<String, TreeContext> ptc;
    protected Map<String, TreeContext> ctc;

    public ASTDiffConvertor(String srcPath, ProjectASTDiff projectASTDiff) {
        this.srcPath = srcPath;
        this.projectASTDiff = projectASTDiff;
        projectASTDiff.getDiffSet().forEach(astDiff -> {
            if (astDiff.getSrcPath().equals(srcPath))
            {
                this.rm_astDiff = astDiff;
            }
        });
        ptc = projectASTDiff.getParentContextMap();
        ctc = projectASTDiff.getChildContextMap();
    }

    protected abstract List<MappingExportModel> getExportedMappings();

    public ASTDiff makeASTDiff() {
        ASTDiff result = make(getExportedMappings());
        result.computeEditScript(ptc, ctc);
        return result;
    }

    protected ASTDiff make(List<MappingExportModel> exportedMappings){
        return new ASTDiff(this.rm_astDiff.getSrcPath(),
                           this.rm_astDiff.getDstPath(),
                            rm_astDiff.src,
                            rm_astDiff.dst,
                            populateMappingStore(exportedMappings, ptc, ctc)
        );
    }

    protected ExtendedMultiMappingStore populateMappingStore(List<MappingExportModel> exportedMappings, Map<String, TreeContext> ptc, Map<String, TreeContext> ctc) {
        Tree src = ptc.get(rm_astDiff.getSrcPath()).getRoot();
        Tree dst = ctc.get(rm_astDiff.getDstPath()).getRoot();
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(src, dst);
        for (MappingExportModel mapping : exportedMappings) {
            Tree srcNode = TreeUtilFunctions.getTreeBetweenPositionsSecure(src, mapping.getFirstPos(), mapping.getFirstEndPos(),mapping.getFirstType(), mapping.getFirstParentType(), mapping.getFirstLabel());
            Tree dstNode = TreeUtilFunctions.getTreeBetweenPositionsSecure(dst, mapping.getSecondPos(), mapping.getSecondEndPos(),mapping.getSecondType(), mapping.getSecondParentType(), mapping.getSecondLabel());
            if (srcNode == null || dstNode == null)
            {
                if (srcNode != null || dstNode != null)
                {
                    //inter-file mappings
                    if (srcNode == null) {
                        for (Map.Entry<String, TreeContext> stringTreeContextEntry : ptc.entrySet()) {
                            TreeContext value = stringTreeContextEntry.getValue();
                            Tree treeBetweenPositionsSecure = TreeUtilFunctions.getTreeBetweenPositionsSecure(value.getRoot(), mapping.getFirstPos(), mapping.getFirstEndPos(), mapping.getFirstType(), mapping.getFirstParentType(), mapping.getFirstLabel());
                            if (treeBetweenPositionsSecure != null &&
                                    treeBetweenPositionsSecure.getLabel().equals(mapping.getFirstLabel())) {
                                srcNode = treeBetweenPositionsSecure;
                                break;
                            }
                        }
                    }
                    else {
                        for (Map.Entry<String, TreeContext> stringTreeContextEntry : ctc.entrySet()) {
                            TreeContext value = stringTreeContextEntry.getValue();
                            Tree treeBetweenPositionsSecure = TreeUtilFunctions.getTreeBetweenPositionsSecure(value.getRoot(), mapping.getSecondPos(), mapping.getSecondEndPos(), mapping.getSecondType(), mapping.getSecondParentType(), mapping.getSecondLabel());
                            if (treeBetweenPositionsSecure != null &&
                                    treeBetweenPositionsSecure.getLabel().equals(mapping.getSecondLabel())) {
                                dstNode = treeBetweenPositionsSecure;
                                break;
                            }
                        }
                    }
                }
                else{
                    throw new RuntimeException(String.valueOf(mapping));
//                    continue;
                }
            }
            mappings.addMapping(srcNode, dstNode);
        }
        return mappings;
    }
}
