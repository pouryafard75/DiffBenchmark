package benchmark.generators.hrd;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.models.hrd.NecessaryMappings;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.utils.Constants;

/* Created by pourya on 2024-10-06*/
public class HRDGenOnlyComments extends HumanReadableDiffGenerator {
    public HRDGenOnlyComments(IBenchmarkCase benchmarkCase, ASTDiff current) {
        super(benchmarkCase, current);
    }

    @Override
    public void handleProgramElement(MappingMetaInformation mappingMetaInformation) {
    }

    @Override
    public void handleNonProgramElements(MappingMetaInformation mappingMetaInformation) {
        handleComments(mappingMetaInformation.mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
    }

    private void handleComments(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        String name = mapping.first.getType().name;
        if (name.equals(Constants.BLOCK_COMMENT) || name.equals(Constants.LINE_COMMENT)) {
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
        }
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

    @Override
    public void handleMethodDeclaration(MappingMetaInformation mappingMetaInformation) {

    }
}
