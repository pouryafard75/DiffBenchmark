package benchmark.generators.hrd;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.metrics.computers.filters.NoFilter;
import benchmark.models.AbstractMapping;

import benchmark.utils.Experiments.IQuerySelector;
import com.github.gumtreediff.matchers.Mapping;

import static benchmark.generators.hrd.GeneratorUtils.*;
import static benchmark.generators.hrd.GeneratorUtils.generateClassSignature;

/* Created by pourya on 2023-09-15 4:26 p.m. */
public class HRDGen2 extends HumanReadableDiffGenerator {
    public HRDGen2(IBenchmarkCase benchmarkCase, IQuerySelector querySelector, NoFilter noFilter) {
        super(benchmarkCase, querySelector);
    }

    @Override
    public void handleTypeDeclaration(MappingMetaInformation mappingMetaInformation) {
        Mapping mapping = mappingMetaInformation.mapping;
        addAccordingly(
                new AbstractMapping(mapping,
                        generateClassSignature(mapping.first,getSrcContent(mappingMetaInformation.currSrc)),
                        generateClassSignature(mapping.second,getDstContent(mappingMetaInformation.currDst))),
                mappingMetaInformation.target);
    }
    @Override
    public void handleMethodDeclaration(MappingMetaInformation mappingMetaInformation) {
        Mapping mapping = mappingMetaInformation.mapping;
        addAccordingly(
                new AbstractMapping(
                        mapping,
                        generateMethodSignature(mapping.first,getSrcContent(mappingMetaInformation.currSrc)),
                        generateMethodSignature(mapping.second,getDstContent(mappingMetaInformation.currDst))),
                mappingMetaInformation.target);
    }
    @Override
    public void handleFieldDeclaration(MappingMetaInformation mappingMetaInformation) {
        Mapping mapping = mappingMetaInformation.mapping;
        addAccordingly(
                new AbstractMapping(
                        mapping,generateFieldSignature(
                        mapping.first,getSrcContent(mappingMetaInformation.currSrc)),
                        generateFieldSignature(mapping.second,getDstContent(mappingMetaInformation.currDst))),
                mappingMetaInformation.target);
    }
    @Override
    public void handleNonProgramElements(MappingMetaInformation mappingMetaInformation) {
        Mapping mapping = mappingMetaInformation.mapping;
        handleStatement(mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
    }
    @Override
    public void handleEnumDeclaration(MappingMetaInformation mappingMetaInformation) {
        Mapping mapping = mappingMetaInformation.mapping;
        addAccordingly(
                new AbstractMapping(
                        mapping,
                        generateEnumSignature(mapping.first,getSrcContent(mappingMetaInformation.currSrc)),
                        generateEnumSignature(mapping.second,getDstContent(mappingMetaInformation.currDst))),
                mappingMetaInformation.target);
//        throw new RuntimeException("Not implemented yet");
    }
}
