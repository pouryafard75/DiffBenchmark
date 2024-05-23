package benchmark.generators.hrd;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

/* Created by pourya on 2024-04-06*/
public class HRDGenExperimental extends HumanReadableDiffGenerator {

    public HRDGenExperimental(ProjectASTDiff projectASTDiff, ASTDiff generated, CaseInfo info, Configuration configuration) {
        super(projectASTDiff, generated, info, configuration);
    }

    @Override
    public void handleMethodDeclaration(MappingMetaInformation mappingMetaInformation) {

    }

    @Override
    public void handleNonProgramElements(MappingMetaInformation mappingMetaInformation) {

    }

    @Override
    public void handleEnumDeclaration(MappingMetaInformation mappingMetaInformation) {

    }

    @Override
    public void handleFieldDeclaration(MappingMetaInformation mappingMetaInformation) {

    }

    @Override
    public void handleTypeDeclaration(MappingMetaInformation mappingMetaInformation) {

    }
}
