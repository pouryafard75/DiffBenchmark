package benchmark.generators.tools.runners.converter;

import benchmark.generators.tools.runners.trivial.TrivialDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.actions.editscript.SimplifiedExtendedChawatheScriptGenerator;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.util.List;

import static benchmark.utils.Helpers.runWhateverForRM2;

/* Created by pourya on 2024-02-19*/
public class RM2 extends AbstractASTDiffProviderFromExportedMappings {

    public RM2(ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo info, Configuration conf) {
        super(projectASTDiff, input, info, conf);
    }

    @Override
    protected List<MappingExportModel> getExportedMappings() {
        rm2.refactoringminer.astDiff.actions.ProjectASTDiff proj = runWhateverForRM2(info);
        for (rm2.refactoringminer.astDiff.actions.ASTDiff astDiff : proj.getDiffSet()) {
            if (astDiff.getSrcPath().equals(input.getSrcPath()))
                return MappingExportModel.exportModelList(astDiff.getAllMappings().getMappings());
        }
        return null;
    }
    @Override
    public ASTDiff makeASTDiff() {
        ASTDiff astDiff = make(getExportedMappings());
        postPopulation(astDiff);
        astDiff.computeEditScript(ptc, ctc, new SimplifiedExtendedChawatheScriptGenerator());
        return astDiff;
    }
    private void postPopulation(ASTDiff astDiff) {
        ASTDiff trivialDiff = new TrivialDiff(projectASTDiff, input, info, conf).makeASTDiff();
        for (Mapping m : trivialDiff.getAllMappings())  astDiff.getAllMappings().addMapping(m.first, m.second);
    }
}
