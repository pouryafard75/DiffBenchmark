package benchmark.metrics.computers.violation.modifier;

import com.github.gumtreediff.matchers.Mapping;

/* Created by pourya on 2023-12-11 9:44 p.m. */
public enum ModifierKind {
    ACCESS_MODIFIER,
    SEALED_MODIFIER,
    STATIC_MODIFIER,
    ABSTRACT_MODIFIER,
    NATIVE_MODIFIER,
    SYNCHRONIZED_MODIFIER,
    TRANSIENT_MODIFIER,
    VOLATILE_MODIFIER,
    STRICTFP_MODIFIER,
    DEFAULT_MODIFIER;
    static ModifierKind getType(String label){
        switch (label) {
            case "public": case "private": case "protected": case "default": return ModifierKind.ACCESS_MODIFIER;
            case "sealed": case "final": case "non-sealed": return ModifierKind.SEALED_MODIFIER;
            case "static": return ModifierKind.STATIC_MODIFIER;
            case "abstract": return ModifierKind.ABSTRACT_MODIFIER;
            case "native": return ModifierKind.NATIVE_MODIFIER;
            case "synchronized": return ModifierKind.SYNCHRONIZED_MODIFIER;
            case "transient": return ModifierKind.TRANSIENT_MODIFIER;
            case "volatile": return ModifierKind.VOLATILE_MODIFIER;
            case "strictfp": return ModifierKind.STRICTFP_MODIFIER;
            default:
                throw new RuntimeException("Unknown modifier label: " + label);
        }
    }
    public static boolean isViolation(Mapping mapping)
    {
        ModifierKind typeFirst = getType(mapping.first.getLabel());
        ModifierKind typeSecond = getType(mapping.second.getLabel());
        return typeFirst != typeSecond;
    }
}
