package benchmark.metrics.models;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.AdditionalASTConstants;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import static benchmark.oracle.generators.GeneratorUtils.*;
import static benchmark.oracle.generators.GeneratorUtils.generateFieldSignature;
import static benchmark.oracle.generators.HumanReadableDiffGenerator.*;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.isStatement;

/* Created by pourya on 2023-04-16 5:07 a.m. */
public class DiffIgnore {
    private final Tree src;
    private final Tree dst;
    private final ExtendedMultiMappingStore mappings;
//    private final HumanReadableDiff computed = new HumanReadableDiff();
    private final Set<AbstractMapping> ignoredMappingSet = new HashSet<>();
    private int _ignoredElements;
    private int _ignoredMappings;


    public DiffIgnore(Tree src, Tree dst, ExtendedMultiMappingStore mappings) {
        this.src = src;
        this.dst = dst;
        this.mappings = mappings;
        this.make();
    }

    private void make(){
        this.run2();
    }
    private void run2(){
        for (Mapping mapping : mappings) {
            if (isInterFileMapping(mapping,src,dst)) continue;
            if (isPartOfJavadoc(mapping)) continue;
            if (isBetweenDifferentTypes(mapping)) continue;
            if (!mapping.first.isIsomorphicTo(mapping.second)) continue;
            if (!fromSameEnclosingClass(mapping)) continue;
            //Second type definitely has the same type due to the previous check
            switch (mapping.first.getType().name) //Second type definitely has the same type due to the previous check
            {
                case Constants.TYPE_DECLARATION:
                    _ignoredMappings += getClassSignatureMappings(mapping,mappings).size();
                    _ignoredElements += 1;
                    break;
                case Constants.METHOD_DECLARATION:
                    _ignoredMappings += getMethodSignatureMappings(mapping,mappings).size();
                    _ignoredElements += 1;
                    break;
                case Constants.FIELD_DECLARATION:
//                    _ignoredMappings += getFieldSignatureMappings(mapping,mappings).size();
                    _ignoredElements += 1;
                    break;
            }
            if (isStatement(mapping.first.getType().name)) {
                Tree srcEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.first.getParent(), Constants.METHOD_DECLARATION);
                Tree dstEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.second.getParent(), Constants.METHOD_DECLARATION);
                if (srcEnclosingMethod != null && dstEnclosingMethod != null)
                    if (srcEnclosingMethod.isIsomorphicTo(dstEnclosingMethod)) {
                        _ignoredMappings += 1;
                    }
            }
        }
    }

    private void handleMethodDeclaration(Mapping mapping) {
        
    }

    private void handleTypeDeclaration(Mapping mapping) {
        
    }

    private void addAccordingly(AbstractMapping abstractMapping) {
        //TODO MAKE IT WORK FOR DIFFERENT TYPES
        if (!abstractMapping.getLeftType().equals(abstractMapping.getRightType()))
            return;
        String leftType = abstractMapping.getLeftType();
        if (leftType.equals(Constants.TYPE_DECLARATION) ||
                leftType.equals(Constants.METHOD_DECLARATION) ||
                leftType.equals(Constants.FIELD_DECLARATION))
            _ignoredElements ++;
        else
            _ignoredMappings ++;
    }
    private void run(){
        for (Mapping mapping : mappings) {
            if (isInterFileMapping(mapping,src,dst)) continue;
            if (mapping.first.getType().name.equals(Constants.METHOD_DECLARATION))
            {
                if (!fromSameEnclosingClass(mapping)) continue;
                if (mapping.first.isIsomorphicTo(mapping.second))
                {
                    Tree srcEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.first.getParent(), Constants.METHOD_DECLARATION);
                    Tree dstEnclosingMethod = TreeUtilFunctions.getParentUntilType(mapping.second.getParent(), Constants.METHOD_DECLARATION);
                    if (srcEnclosingMethod != null && dstEnclosingMethod != null)
                    {
                        if (srcEnclosingMethod.isIsomorphicTo(dstEnclosingMethod)) {
                            _ignoredElements += 1;
                            _ignoredMappings += getNumberOfSignatureIgnoresForIsomorphic(mapping);
                            continue;
                        }
                    }
                    _ignoredElements += 1;
                    List<Tree> descendants = mapping.first.getDescendants();
                    for (Tree descendant : descendants) {
                        if (isStatement(descendant.getType().name))
                            _ignoredMappings += 1;
                    }
                    _ignoredMappings += getNumberOfSignatureIgnoresForIsomorphic(mapping);
                }
            }
            if (mapping.first.getType().name.equals(Constants.TYPE_DECLARATION))
            {
                if (mapping.first.isIsomorphicTo(mapping.second))
                {
                    _ignoredElements += 1;
                    List<Tree> descendants = mapping.first.getDescendants();
                    for (Tree descendant : descendants) {
                        if (isStatement(descendant.getType().name))
                            _ignoredMappings += 1;
                    }
                    _ignoredMappings += getNumberOfSignatureIgnoresForIsomorphic(mapping);
                }
            }
        }
    }

    private static boolean fromSameEnclosingClass(Mapping mapping) {
        Tree srcEnclosingClass = TreeUtilFunctions.getParentUntilType(mapping.first, Constants.TYPE_DECLARATION);
        Tree dstEnclosingClass = TreeUtilFunctions.getParentUntilType(mapping.second, Constants.TYPE_DECLARATION);
        //TODO MAKE IT WORK FOR IMPORTS AND PACKAGES!!
        if (srcEnclosingClass == null || dstEnclosingClass == null) return false;
        Tree srcClassSimpleName = firstChildOfType(srcEnclosingClass,Constants.SIMPLE_NAME);
        Tree dstClassSimpleName = firstChildOfType(dstEnclosingClass,Constants.SIMPLE_NAME);
        if (srcClassSimpleName == null || dstClassSimpleName == null) return false;
        return srcClassSimpleName.getLabel().equals(dstClassSimpleName.getLabel());
    }

    private static Tree firstChildOfType(Tree srcEnclosingClass, String inputType) {
        for (Tree child : srcEnclosingClass.getChildren()) {
            if (child.getType().name.equals(inputType))
            {
                return child;
            }
        }
        return null;
    }

    private int getNumberOfSignatureIgnoresForIsomorphic(Mapping mapping) {
        int result = 0;
        for (Tree child : mapping.first.getChildren()) {
            if (child.getType().name.equals(Constants.BLOCK)) continue;
            if (child.getType().name.equals(AdditionalASTConstants.JAVA_DOC)) continue;
            result += 1;
        }
        return result;
    }
    public int getNumberOfIgnoredMappings() {
        return this._ignoredMappings;
    }
    public int getNumberOfIgnoredElements() {
        return this._ignoredElements;
    }
}
