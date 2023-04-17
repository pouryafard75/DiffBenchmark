package benchmark.Oracle;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.Constants;

import java.util.List;

/* Created by pourya on 2023-04-17 6:45 p.m. */
public class BenchmarkUtils {
    public static String generateFieldSignature(Tree fieldDeclration) {
        StringBuilder sb = new StringBuilder();
        outer:
        for (Tree child : fieldDeclration.getChildren()) {
            if (child.getType().name.equals(Constants.VARIABLE_DECLARATION_FRAGMENT))
                for (Tree childChild : child.getChildren()) {
                    if (childChild.getType().name.equals(Constants.SIMPLE_NAME)) {
                        sb.append(childChild.getLabel());
                        break outer;
                    }
                }
        }
        return sb.toString();
    }
    public static String generateClassSignature(Tree typeDeclaration) {
        StringBuilder sb = new StringBuilder();
        for (Tree child : typeDeclaration.getChildren()) {
            if (child.getType().name.equals(Constants.TYPE_DECLARATION_KIND))
            {
                sb.append(child.getLabel()).append(" : ");
            }
            if (child.getType().name.equals(Constants.SIMPLE_NAME))
            {
                sb.append(child.getLabel());
                break;
            }
        }
        return sb.toString();
    }
    public static String generateMethodSignature(Tree methodDeclaration) {
        StringBuilder sb = new StringBuilder();
        List<Tree> children = methodDeclaration.getChildren();
        String returnType = null;
        boolean accessModifierFound = false;
        boolean bodyFound = false;
        for (int i = 0; i < children.size(); i++) {
            Tree child = children.get(i);
            if (child.getType().name.equals("SimpleName")) {
                sb.append(child.getLabel()).append("(");
            } else if (isTypeNode(child) && returnType == null) {
                returnType = child.getLabel();
            } else if (child.getType().name.equals("Modifier")) {
                if (child.getLabel().equals("public") ||
                        child.getLabel().equals("private") ||
                        child.getLabel().equals("protected")) {
                    sb.append(child.getLabel()).append(" ");
                    accessModifierFound = true;
                }
                else if (child.getLabel().equals("abstract")) {
                    sb.append(child.getLabel()).append(" ");
                }
                else if (child.getLabel().equals("default")) {
                    sb.append("public ");
                    accessModifierFound = true;
                }
            } else if (child.getType().name.equals("SingleVariableDeclaration")) {
                String type = null;
                String name = null;
                for (Tree child2 : child.getChildren()) {
                    if (isTypeNode(child2)) {
                        type = child2.getLabel();
                    } else if (child2.getType().name.equals("SimpleName")) {
                        name = child2.getLabel();
                    }
                }
                if (name != null && type != null) {
                    if ((name.endsWith("s") || name.endsWith("List")) && i == children.size() - 2 && children.get(i + 1).getType().name.equals("Block") &&
                            !type.endsWith("[]") && !type.contains("List") && !type.contains("Collection") && !type.contains("Iterable") && !type.contains("Set") && !type.contains("Iterator") && !type.contains("Array") &&
                            !type.endsWith("s") && !type.toLowerCase().contains(name.toLowerCase()) &&
                            !name.endsWith("ss") && !type.equals("boolean") && !type.equals("int")) {
                        //hack for varargs
                        sb.append(name + " " + type + "...");
                    } else {
                        sb.append(name + " " + type);
                    }
                }
                if (i < children.size() - 1 && children.get(i + 1).getType().name.equals("SingleVariableDeclaration")) {
                    sb.append(",").append(" ");
                }
            } else if (child.getType().name.equals("Block")) {
                bodyFound = true;
            }
        }
        sb.append(")");
        if (returnType != null) {
            sb.append(" : ").append(returnType);
        }
        if (!accessModifierFound) {
            if (bodyFound) {
                return "package " + sb.toString();
            } else {
                return "public " + sb.toString();
            }
        }
        return sb.toString();
    }
    private static boolean isTypeNode(Tree child) {
        return child.getType().name.equals("SimpleType") ||
                child.getType().name.equals("PrimitiveType") ||
                child.getType().name.equals("QualifiedType") ||
                child.getType().name.equals("WildcardType") ||
                child.getType().name.equals("ArrayType") ||
                child.getType().name.equals("ParameterizedType") ||
                child.getType().name.equals("NameQualifiedType");
    }
}
