package benchmark.oracle.generators.tools.runners;

import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.Mapping;
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
    }

    protected abstract List<MappingExportModel> getExportedMappings();

    public ASTDiff makeASTDiff() {
        ASTDiff astDiff = make(getExportedMappings());
        astDiff.computeEditScript(ptc, ctc);
        return astDiff;
    }

    protected ASTDiff make(List<MappingExportModel> exportedMappings){
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(rm_astDiff.src.getRoot(),rm_astDiff.dst.getRoot());
        ptc = new HashMap<>();
        ctc = new HashMap<>();
        for (Map.Entry<String, String> stringStringEntry : projectASTDiff.getFileContentsBefore().entrySet()) {
            String key = stringStringEntry.getKey();
            String content = stringStringEntry.getValue();
            try {
                ptc.put(key, new JdtTreeGenerator().generateFrom().string(content));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (Map.Entry<String, String> stringStringEntry : projectASTDiff.getFileContentsAfter().entrySet()) {
            String key = stringStringEntry.getKey();
            String content = stringStringEntry.getValue();
            try {
                ctc.put(key, new JdtTreeGenerator().generateFrom().string(content));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        populateMappingsFromJson(mappings, rm_astDiff, exportedMappings, ptc, ctc);
        return new ASTDiff(this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath(), rm_astDiff.src, rm_astDiff.dst, mappings);
    }

    protected static void populateMappingsFromJson(ExtendedMultiMappingStore mappings, ASTDiff rmAstDiff, List<MappingExportModel> exportedMappings, Map<String, TreeContext> ptc, Map<String, TreeContext> ctc) {
        Tree src = rmAstDiff.src.getRoot();
        Tree dst = rmAstDiff.dst.getRoot();
        for (MappingExportModel perfectMapping : exportedMappings) {
            Tree srcNode = TreeUtilFunctions.getTreeBetweenPositionsSecure(src, perfectMapping.getFirstPos(), perfectMapping.getFirstEndPos(),perfectMapping.getFirstType(), perfectMapping.getFirstParentType(), perfectMapping.getFirstLabel());
            Tree dstNode = TreeUtilFunctions.getTreeBetweenPositionsSecure(dst, perfectMapping.getSecondPos(), perfectMapping.getSecondEndPos(),perfectMapping.getSecondType(), perfectMapping.getSecondParentType(), perfectMapping.getSecondLabel());
            if (srcNode == null || dstNode == null)
            {
                if (srcNode != null || dstNode != null)
                {
                    //inter-file mappings
                    if (srcNode == null) {
                        for (Map.Entry<String, TreeContext> stringTreeContextEntry : ptc.entrySet()) {
                            TreeContext value = stringTreeContextEntry.getValue();
                            Tree treeBetweenPositionsSecure = TreeUtilFunctions.getTreeBetweenPositionsSecure(value.getRoot(), perfectMapping.getFirstPos(), perfectMapping.getFirstEndPos(), perfectMapping.getFirstType(), perfectMapping.getFirstParentType(), perfectMapping.getFirstLabel());
                            if (treeBetweenPositionsSecure != null &&
                                    treeBetweenPositionsSecure.getLabel().equals(perfectMapping.getFirstLabel())) {
                                srcNode = treeBetweenPositionsSecure;
                                break;
                            }
                        }
                    }
                    else {
                        for (Map.Entry<String, TreeContext> stringTreeContextEntry : ctc.entrySet()) {
                            TreeContext value = stringTreeContextEntry.getValue();
                            Tree treeBetweenPositionsSecure = TreeUtilFunctions.getTreeBetweenPositionsSecure(value.getRoot(), perfectMapping.getSecondPos(), perfectMapping.getSecondEndPos(), perfectMapping.getSecondType(), perfectMapping.getSecondParentType(), perfectMapping.getSecondLabel());
                            if (treeBetweenPositionsSecure != null &&
                                    treeBetweenPositionsSecure.getLabel().equals(perfectMapping.getSecondLabel())) {
                                dstNode = treeBetweenPositionsSecure;
                                break;
                            }
                        }
                    }
                }
                else{
                    throw new RuntimeException(String.valueOf(perfectMapping));
//                    continue;
                }
            }
            mappings.addMapping(srcNode, dstNode);
        }
    }
}
