package benchmark.generators.hrd;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.utils.Constants;

/* Created by pourya on 2023-04-17 6:45 p.m. */
public class GeneratorUtils {
    public static String generateFieldSignature(Tree fieldDeclaration, String content) {
        int end = fieldDeclaration.getEndPos();
        int start = fieldDeclaration.getPos();
        return removeJavaDoc(content.substring(start, end)).trim();
    }
    public static String generateClassSignature(Tree typeDeclaration, String content) {
        StringBuilder sb = new StringBuilder();
        if (typeDeclaration.getLabel().equals("")) {
            for (Tree child : typeDeclaration.getChildren()) {
//                if (child.getType().name.equals(Constants.TYPE_DECLARATION_KIND)) {
//                    sb.append(child.getLabel()).append(" : ");
//                }
                if (child.getType().name.equals(Constants.SIMPLE_NAME)) {
                    sb.append(child.getLabel());
                    break;
                }
            }
        }
        else {
            sb.append(typeDeclaration.getLabel());
        }
        return sb.toString();
    }
    public static int reverseSearch(String text, String wordToFind, int startingIndex) {
        for (int i = startingIndex; i >= 0; i--) {
            if (text.regionMatches(true, i, wordToFind, 0, wordToFind.length())) {
                return i; // Found the word, return the index
            }
        }
        return -1; // Word not found
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

        return removeJavaDoc(content.substring(start, end)).trim();
    }
    private static String removeJavaDoc(String content) {
//        String javaDocPattern = "/\\*\\*.*?\\*/"; // Matches everything between /** and */
//        return content.replaceAll(javaDocPattern, "");

        return content.replaceAll("(?s)/\\*.*?\\*/", "");

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
