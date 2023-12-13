package benchmark.metrics.computers.violation.models;

import benchmark.metrics.computers.violation.modifier.ModifierKind;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.Constants;

import java.util.function.Predicate;

import static benchmark.metrics.computers.violation.constructor.Helpers.*;

/* Created by pourya on 2023-12-10 9:01 p.m. */
public enum ViolationKind
{
    BLOCK((mapping) -> {
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

    SIMPLE_NAME((mapping) -> {
        if (havingSameParentsType().test(mapping)) return false;
        if (mapping.first.getType().name.equals(Constants.SIMPLE_NAME))
            return !mapping.first.getLabel().equals(mapping.second.getLabel());
        else
            return false;
    }),

    TYPE((mapping) -> {
            if (havingSameParentsType().test(mapping)) return false;
            return getJdtTypes().contains(mapping.first.getType().name);
    }),

    SINGLE_VARIABLE_DECL((mapping) -> {
            if (havingSameParentsType().test(mapping)) return false;
            else return mapping.first.getType().name.equals("SingleVariableDeclaration");
    }),

    MODIFIER((mapping) -> {
                if (mapping.first.getType().name.equals("Modifier")) {
                    return ModifierKind.isViolation(mapping);
                } else
                    return false;
    }),
    CONSTRUCTOR((mapping) -> {
        return isConstructor(mapping.first) != isConstructor(mapping.second);
    })
    ;


    private static boolean isConstructor(Tree tree) {
        if (tree.getType().name.equals(Constants.METHOD_DECLARATION)) {
            return findChildByTypes(tree, getJdtTypes()) == null;
        }
        return false;
    }


    public static Predicate<Mapping> havingSameParentsType() {
        return mapping -> {
            if (mapping.first.getParent() == null || mapping.second.getParent() == null) return false;
            return mapping.first.getParent().getType().name.equals(mapping.second.getParent().getType().name);
        };
    }

    private final Predicate<Mapping> condition;
    ViolationKind(Predicate<Mapping> condition) {
        this.condition = condition;
    }

    public Predicate<Mapping> getCondition() {
        return condition;
    }
}