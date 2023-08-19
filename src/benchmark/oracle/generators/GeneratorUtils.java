package benchmark.oracle.generators;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.Constants;

import java.util.List;

/* Created by pourya on 2023-04-17 6:45 p.m. */
public class GeneratorUtils {
    public static String generateFieldSignature(Tree fieldDeclration, String content) {
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
    public static String generateClassSignature(Tree typeDeclaration, String content) {
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
    public static String generateMethodSignature(Tree methodDeclaration, String content) {
        int end = methodDeclaration.getEndPos();
        int start = methodDeclaration.getPos();
        for (Tree child : methodDeclaration.getChildren()) {
            if (child.getType().name.equals(Constants.BLOCK))
            {
                end = child.getPos() - 1;
            }
            else if (child.getType().name.equals(Constants.JAVA_DOC) || child.getType().name.contains("Annotation"))
            {
                start = child.getPos() + child.getLength() + 1;
            }
        }
        return content.substring(start, end).trim();
    }
    private void a ( ) { }
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
