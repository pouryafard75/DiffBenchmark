package benchmark.generators.hrd;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.ExperimentConfiguration;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

/* Created by pourya on 2024-04-06*/
public class HRDGenExperimental extends HumanReadableDiffGenerator {

    public HRDGenExperimental(ProjectASTDiff projectASTDiff, ASTDiff generated, BenchmarkCase info, ExperimentConfiguration experimentConfiguration) {
        super(projectASTDiff, generated, info, experimentConfiguration);
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
