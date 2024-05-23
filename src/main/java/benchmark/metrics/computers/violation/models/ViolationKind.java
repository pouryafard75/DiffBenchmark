package benchmark.metrics.computers.violation.models;

import benchmark.metrics.computers.violation.modifier.ModifierKind;
import benchmark.generators.tools.ASTDiffTool;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.utils.Constants;

import java.util.function.Predicate;

import static benchmark.metrics.computers.violation.constructor.Helpers.*;
import static benchmark.metrics.computers.violation.simplename.GT2Helpers.findGT2TranslatedParentTypeForMethodInvocations;

/* Created by pourya on 2023-12-10 9:01 p.m. */
public enum ViolationKind
{
    BLOCK((mapping, tool, perfect) -> {
        if (havingSameParentsType().test(mapping)) return false;
        if (mapping.first.getType().name.equals(Constants.BLOCK))
        {
            if (mapping.first.getParent().getType().name.equals(Constants.METHOD_DECLARATION)
                    & !mapping.second.getParent().getType().name.equals(Constants.METHOD_DECLARATION))
                return true;
            else return !mapping.first.getParent().getType().name.equals(Constants.METHOD_DECLARATION)
                    & mapping.second.getParent().getType().name.equals(Constants.METHOD_DECLARATION);
        }
        else
            return false;
    }),

    SIMPLE_NAME((mapping, tool, perfect) -> {
        boolean baseCondition = SimpleNameGeneralLogic(mapping);
        if (!baseCondition) return false;
        if (tool.equals(ASTDiffTool.GT2) || tool.equals(ASTDiffTool.IJM) || tool.equals(ASTDiffTool.MTD)) {
            return GT2_SIMPLE_NAME_LOGIC(mapping, perfect);
        }
        return !havingSameParentsType().test(mapping);
    }),

    TYPE((mapping, tool, perfect) -> {
            if (havingSameParentsType().test(mapping)) return false;
            return getJdtTypes().contains(mapping.first.getType().name);
    }),

    SINGLE_VARIABLE_DECL((mapping, tool, perfect) -> {
            if (havingSameParentsType().test(mapping)) return false;
            else return mapping.first.getType().name.equals("SingleVariableDeclaration");
    }),

    MODIFIER((mapping, tool, perfect) -> {
                if (mapping.first.getType().name.equals("Modifier")) {
                    return ModifierKind.isViolation(mapping);
                } else
                    return false;
    }),
    CONSTRUCTOR((mapping, tool, perfect) -> {
        return isConstructor(mapping.first) != isConstructor(mapping.second);
    })
    ;

    private static boolean SimpleNameGeneralLogic(Mapping mapping) {
        if (mapping.first.getType().name.equals(Constants.SIMPLE_NAME))
            return !mapping.first.getLabel().equals(mapping.second.getLabel());
        else
            return false;
    }


    private static boolean isConstructor(Tree tree) {
        if (tree.getType().name.equals(Constants.METHOD_DECLARATION)) {
            return findChildByTypes(tree, getJdtTypes()) == null;
        }
        return false;
    }


    private static Predicate<Mapping> havingSameParentsType() {
        return mapping -> {
            if (mapping.first.getParent() == null || mapping.second.getParent() == null) return false;
            return mapping.first.getParent().getType().name.equals(mapping.second.getParent().getType().name);
        };
    }

    private final ViolationKindCondition condition;
    ViolationKind(ViolationKindCondition condition) {
        this.condition = condition;
    }

    public ViolationKindCondition getCondition() {
        return condition;
    }

    private static boolean GT2_SIMPLE_NAME_LOGIC(Mapping m, ASTDiff perfect){
        if (m.first.getParent() == null || m.second.getParent() == null) return false;
//        if (m.first.getParent().getType().name.equals(Constants.METHOD_INVOCATION) && m.second.getParent().getType().name.equals(Constants.METHOD_INVOCATION))
        {
            String p1 = findGT2TranslatedParentTypeForMethodInvocations(m.first, perfect.src.getRoot());
            String p2 = findGT2TranslatedParentTypeForMethodInvocations(m.second, perfect.dst.getRoot());
            if (p1 == null || p2 == null) return false;
            return !p1.equals(p2);
        }
//        return SimpleNameGeneralLogic(m);
    }

}