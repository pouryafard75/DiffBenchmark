package benchmark.oracle.generators.tools.runners;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.util.List;

import static benchmark.utils.Helpers.runWhateverForRM2;

/* Created by pourya on 2024-02-19*/
public class RM2 extends ASTDiffConvertor {
    private final CaseInfo info;
    private final Configuration configuration;

    public RM2(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, CaseInfo caseInfo, Configuration configuration) {
        super(rm_astDiff.getSrcPath(), projectASTDiff);
        this.info = caseInfo;
        this.rm_astDiff = rm_astDiff;
        this.configuration = configuration;
    }

    @Override
    protected List<MappingExportModel> getExportedMappings() {
        rm2.refactoringminer.astDiff.actions.ProjectASTDiff proj = runWhateverForRM2(info);
        for (rm2.refactoringminer.astDiff.actions.ASTDiff astDiff : proj.getDiffSet()) {
            if (astDiff.getSrcPath().equals(rm_astDiff.getSrcPath()))
                return MappingExportModel.exportModelList(astDiff.getAllMappings().getMappings());
        }
        return null;
    }
    @Override
    public ASTDiff makeASTDiff() {
        ASTDiff astDiff = make(getExportedMappings());
        postPopulation(astDiff);
        astDiff.computeEditScript(ptc, ctc);
        return astDiff;
    }
    private void postPopulation(ASTDiff astDiff) {
        ASTDiff trivialDiff = new TrivialDiff(projectASTDiff, rm_astDiff, info, configuration).makeASTDiff();
        for (Mapping m : trivialDiff.getAllMappings())  astDiff.getAllMappings().addMapping(m.first, m.second);
    }
}
