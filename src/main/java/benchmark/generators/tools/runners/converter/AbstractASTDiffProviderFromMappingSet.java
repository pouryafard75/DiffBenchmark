package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.BenchmarkCase;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.actions.editscript.SimplifiedExtendedChawatheScriptGenerator;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2024-02-19*/
public abstract class AbstractASTDiffProviderFromMappingSet extends AbstractASTDiffProviderFromExportedMappings {
    protected final String srcContents;
    protected final String dstContents;

    public AbstractASTDiffProviderFromMappingSet(ProjectASTDiff projectASTDiff, ASTDiff input, BenchmarkCase info) {
        super(projectASTDiff, input, info);
        this.srcContents = projectASTDiff.getFileContentsBefore().get(input.getSrcPath());
        this.dstContents = projectASTDiff.getFileContentsAfter().get(input.getDstPath());
    }

    @Override
    protected List<MappingExportModel> getExportedMappings() {
        Set<Mapping> mappings;
        try {
            mappings = getMappings();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (mappings == null)
            throw new RuntimeException("Mappings not found");
        else
            return MappingExportModel.exportModelList(mappings);
    }

    @Override
    public ASTDiff makeASTDiff() {
        ASTDiff astDiff = make(getExportedMappings());
        postPopulation(astDiff);
        astDiff.computeEditScript(ptc, ctc, new SimplifiedExtendedChawatheScriptGenerator());
        return astDiff;
    }

    protected void postPopulation(ASTDiff astDiff) {}
    protected abstract Set<Mapping> getMappings() throws IOException;
}
