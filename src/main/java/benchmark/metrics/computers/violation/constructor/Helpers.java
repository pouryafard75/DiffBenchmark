package benchmark.metrics.computers.violation.constructor;

import com.github.gumtreediff.tree.Tree;

import java.util.Collection;
import java.util.Set;

/* Created by pourya on 2023-12-11 9:51 p.m. */
public class Helpers {

    public static Tree findChildByTypes(Tree tree, Collection<String> acceptableTypes) {
        if (!tree.getChildren().isEmpty())
        {
            for (Tree child: tree.getChildren()) {
                if (acceptableTypes.contains(child.getType().name))
                    return child;
            }
        }
        return null;
    }
    public static Set<String> getJdtTypes() {
        return Set.of("PrimitiveType", "SimpleType", "QualifiedType", "NameQualifiedType", "WildcardType", "ArrayType", "ParameterizedType", "UnionType", "IntersectionType");
    }
}
