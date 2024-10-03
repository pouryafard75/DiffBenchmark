package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProviderFromProjectASTDiff;
import benchmark.generators.tools.runners.experimental.interfile.GumTreeProjectMatcher;
import benchmark.utils.Experiments.IQuerySelector;
import com.github.gumtreediff.matchers.Matcher;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.editscript.SimplifiedExtendedChawatheScriptGenerator;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.List;
import java.util.Map;

/* Created by pourya on 2024-02-20*/
public abstract class AbstractASTDiffProviderFromExportedMappings extends ASTDiffProviderFromProjectASTDiff {
    public AbstractASTDiffProviderFromExportedMappings(IBenchmarkCase benchmarkCase, IQuerySelector querySelector) {
        super(benchmarkCase, querySelector);
    }

    protected abstract List<MappingExportModel> getExportedMappings();

    @Override
    public ASTDiff getASTDiff() {
        ASTDiff result = make(getExportedMappings());
        result.computeEditScript(ptc, ctc, new SimplifiedExtendedChawatheScriptGenerator());
        return result;
    }

    protected ASTDiff make(List<MappingExportModel> exportedMappings){
        return new ASTDiff(this.input.getSrcPath(),
                           this.input.getDstPath(),
                            input.src,
                            input.dst,
                            populateMappingStore(exportedMappings, ptc, ctc)
        );
    }

    protected ExtendedMultiMappingStore populateMappingStore(List<MappingExportModel> exportedMappings, Map<String, TreeContext> ptc, Map<String, TreeContext> ctc) {
        Tree src = ptc.get(input.getSrcPath()).getRoot();
        Tree dst = ctc.get(input.getDstPath()).getRoot();
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
