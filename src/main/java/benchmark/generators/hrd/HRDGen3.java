package benchmark.generators.hrd;

import benchmark.models.AbstractMapping;
import benchmark.models.NecessaryMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.Constants;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import java.util.LinkedHashSet;
import java.util.Set;

import static benchmark.generators.hrd.GeneratorUtils.*;

/* Created by pourya on 2023-09-15 4:26 p.m. */
public class HRDGen3 extends HumanReadableDiffGenerator {
    public HRDGen3(ProjectASTDiff projectASTDiff, ASTDiff generated, CaseInfo info, Configuration configuration) {
        super(projectASTDiff, generated, info, configuration);
    }
    @Override
    public void handleTypeDeclaration(MappingMetaInformation mappingMetaInformation) {
        Mapping mapping = mappingMetaInformation.mapping;
        addAccordingly(
                new AbstractMapping(mapping,
                        generateClassSignature(mapping.first,getSrcContent(mappingMetaInformation.currSrc)),
                        generateClassSignature(mapping.second,getDstContent(mappingMetaInformation.currDst))),
                mappingMetaInformation.target);
        if (!mappingMetaInformation._inter)
            addTypeSignatureMappings(mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
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
        if (!mappingMetaInformation._inter)
            addMethodSignatureMappings(mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
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
        if (!mappingMetaInformation._inter)
            addFieldDeclarationSignatureMappings(mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
    }

    @Override
    public void handleNonProgramElements(MappingMetaInformation mappingMetaInformation) {
        Mapping mapping = mappingMetaInformation.mapping;
        handleStatement(mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
        if (mappingMetaInformation._inter) {
            // ignore if its part of import statements for inter-file mappings;
            if (isPartOf(mapping, Constants.IMPORT_DECLARATION)) return;
        }
        if (!mappingMetaInformation._inter) //TODO: We have to remove this condition and test
        {
            handleMove(mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
            handleUpdate(mapping, mappingMetaInformation.target, mappingMetaInformation.currSrc, mappingMetaInformation.currDst);
        }
    }

    void addFieldDeclarationSignatureMappings(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        //TODO: we might introduce this feature later
    }
    void addMethodSignatureMappings(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        for (Mapping signatureMapping : getMethodSignatureMappings(mapping, getAstDiff().getAllMappings()))
            addAccordingly(getAbstractMapping(signatureMapping, srcPath, dstPath), target);
    }
    void addTypeSignatureMappings(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        for (Mapping signatureMapping : getClassSignatureMappings(mapping, getAstDiff().getAllMappings()))
            addAccordingly(getAbstractMapping(signatureMapping, srcPath, dstPath), target);
    }
    void handleUpdate(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        /*Update case*/
        if (!mapping.first.getLabel().equals(mapping.second.getLabel()))
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
    }
    void handleMove(Mapping mapping, NecessaryMappings target, String srcPath, String dstPath) {
        if (mapping.first.getParent() == null || mapping.second.getParent() == null) return;
        Tree firstParent = mapping.first.getParent();
        Tree secondParent = mapping.second.getParent();
        if (getAstDiff().getAllMappings().getDsts(firstParent) == null)
            /*Source's parent is not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
        else if (!getAstDiff().getAllMappings().getDsts(firstParent).contains(secondParent))
            /*Parents are not mapped*/
            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
        //Todo: Check if this is necessary
//        else if (mapping.first.positionInParent() != mapping.second.positionInParent())
//            addAccordingly(getAbstractMapping(mapping, srcPath, dstPath), target);
    }

    public static Set<Mapping> getClassSignatureMappings(Mapping mapping, ExtendedMultiMappingStore mappings) {
        Set<Mapping> abstractMappingSet = new LinkedHashSet<>();
        boolean _met = false;
        for (Tree child : mapping.first.getChildren()) {
            if (isPartOf(child, Constants.JAVA_DOC)) continue;
            if (child.getType().name.equals(Constants.SIMPLE_NAME))
                _met = true;
            else if (_met && !child.getType().name.equals(Constants.SIMPLE_TYPE))
                break;
            Set<Tree> dsts = mappings.getDsts(child);
            if (dsts == null) continue;
            for (Tree mappedDst : dsts) {
                if (sameHierarchy(mappedDst,mapping.second))
                    abstractMappingSet.add(new Mapping(child, mappedDst));
            }
        }
        return abstractMappingSet;
    }
    public static Set<Mapping> getMethodSignatureMappings(Mapping mapping, ExtendedMultiMappingStore mappings) {
        Set<Mapping> abstractMappingSet = new LinkedHashSet<>();
        for (Tree child : mapping.first.getChildren()) {
            if (child.getType().name.equals(Constants.BLOCK)) continue;
            if (isPartOf(child, Constants.JAVA_DOC)) continue;
            Set<Tree> dsts = mappings.getDsts(child);
            if (dsts == null) continue;
            for (Tree mappedDst : dsts) {
                if (sameHierarchy(mappedDst,mapping.second))
                    abstractMappingSet.add(new Mapping(child, mappedDst));
            }
        }
        return abstractMappingSet;
    }
    public static boolean sameHierarchy(Tree node, Tree ancestor) {
        if (node == ancestor) {
            return true;
        }
        if (node.getParent() == null) return false;
        return sameHierarchy(node.getParent(), ancestor);
    }
}
