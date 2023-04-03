package benchmark;

import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.gumtreediff.tree.Tree;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.getTreeBetweenPositions;

/* Created by pourya on 2023-02-08 2:40 a.m. */
public class OracleMaker {
    private final Tree src;
    private final Tree dst;
    private final List<MappingExportModel> mappings;
    private final String srcPath;
    private final String dstPath;
    private final String srcContent;
    private final String dstContent;

    private List<LeftProgramElement> result;

    public OracleMaker(Tree src, Tree dst, List<MappingExportModel> mappings, String srcPath, String dstPath, String srcContent, String dstContent) {
        this.src = src;
        this.dst = dst;
        this.mappings = mappings;
        this.srcPath = srcPath;
        this.dstPath = dstPath;
        this.srcContent = srcContent;
        this.dstContent = dstContent;
        result = new ArrayList<>();
    }

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
    public List<LeftProgramElement> makeForMethods(){
        List<LeftProgramElement> ret = new ArrayList<>();
        List<Tree> srcAll = src.getDescendants();
        for (Tree srcNode : srcAll) {
            if (srcNode.getType().name.equals(Constants.METHOD_DECLARATION))
            {
                Tree srcMethod = srcNode;
                LeftProgramElement leftProgramElement = new LeftProgramElement(generateMethodSignature(srcMethod), findEnclosingClassName(srcMethod), srcPath);
                Set<RightProgramElement> items = new LinkedHashSet<>();
                for (Tree descendant : srcNode.getDescendants()) {
                    if (isStatement(descendant.getType().name))
                    {
                        for (MappingExportModel mapping : mappings) {
                            if (mapping.getFirstPos() == descendant.getPos()
                            && mapping.getFirstEndPos() == descendant.getEndPos()
                            && mapping.getFirstType().equals(descendant.getType().name))
                            {
                                Tree dstElement = getTreeBetweenPositions(dst, mapping.getSecondPos(), mapping.getSecondEndPos(),mapping.getFirstType());
                                if (dstElement == null)
                                    continue;
                                Tree dstMethod = TreeUtilFunctions.getParentUntilType(dstElement, Constants.METHOD_DECLARATION);
                                if (dstMethod == null)
                                    continue;
                                RightProgramElement rightProgramElement = new RightProgramElement(
                                        generateMethodSignature(dstMethod), findEnclosingClassName(dstMethod), dstPath);
                                if (items.contains(rightProgramElement))
                                    continue;
                                items.add(rightProgramElement);
                                ProgramElementMapping programElementMapping = new ProgramElementMapping();
                                programElementMapping.setRightProgramElement(rightProgramElement);
                                programElementMapping.setAbstractMappingList(fromTwoMethodTrees(srcMethod,dstMethod));
                                leftProgramElement.addMapping(programElementMapping);
                            }
                        }
                    }
                }
                ret.add(leftProgramElement);
                for (MappingExportModel mapping : mappings) {
                    if (mapping.getFirstPos() == srcMethod.getPos()
                            && mapping.getFirstEndPos() == srcMethod.getEndPos()
                            && mapping.getFirstType().equals(srcMethod.getType().name))
                    {
                        Tree dstMethod = getTreeBetweenPositions(dst,mapping.getSecondPos(),mapping.getSecondEndPos(),mapping.getSecondType());
                        if (dstMethod == null) continue;
                        RightProgramElement rightProgramElement = new RightProgramElement(
                                generateMethodSignature(dstMethod), findEnclosingClassName(dstMethod), dstPath);
                        for (RightProgramElement item : items) {
                            if (item.equals(rightProgramElement)) {
                                item.setMatch(true);
                            }
                        }
                    }
                }
            }
        }
        return ret;
    }

    private List<AbstractMapping> fromTwoMethodTrees(Tree srcMethod, Tree dstMethod){
        List<AbstractMapping> ret = new ArrayList<>();
        for (MappingExportModel mapping : mappings) {
            if (isStatement(mapping.getFirstType()))
                if (subsumes(srcMethod,mapping.getFirstPos(),mapping.getFirstEndPos())
                    && subsumes(dstMethod,mapping.getSecondPos(),mapping.getSecondEndPos())) {
                AbstractMapping abstractMapping = new AbstractMapping(
                        mapping.getFirstPos(), mapping.getFirstEndPos(),
                        mapping.getSecondPos(), mapping.getSecondEndPos(),
                        getString(srcMethod, srcContent,mapping.getFirstPos(),mapping.getFirstEndPos(),mapping.getFirstType()),
                        getString(dstMethod, dstContent,mapping.getSecondPos(),mapping.getSecondEndPos(),mapping.getSecondType()),
                        mapping.getFirstType(),
                        mapping.getSecondType());
                ret.add(abstractMapping);
            }
        }
        return ret;

    }

    public static String getString(Tree tree, String content, int start, int end, String type) {
        Tree founded = getTreeBetweenPositions(tree, start, end, type);
        int endoff = tree.getEndPos();
        Tree prevChild = tree;
        boolean _flag = false;
        switch (type)
        {
            case Constants.BLOCK:
                return "{...}";
            case Constants.IF_STATEMENT:

                Tree child = founded.getChild(0);
                return content.substring(start,child.getEndPos()) + ")";
            case Constants.ENHANCED_FOR_STATEMENT:
            case Constants.FOR_STATEMENT:
            case Constants.WHILE_STATEMENT:
                endoff = founded.getEndPos();
                for (Tree foundedChild : founded.getChildren()) {
                    if (foundedChild.getType().name.equals(Constants.BLOCK))
                    {
                       endoff = prevChild.getEndPos();
                       _flag = true;
                       break;
                    }
                    prevChild = foundedChild;
                }
                String ret = content.substring(start, endoff);
                if (_flag) ret += ")";
                return ret;
            case Constants.TRY_STATEMENT:
                return "try{...}";
            case Constants.DO_STATEMENT:
                return "do{...}";
        }
        return content.substring(start, end);
    }

    private boolean subsumes(Tree input, int pos, int endPos) {
        return input.getPos() <= pos && input.getEndPos() >= endPos;
    }
    private String findEnclosingClassName(Tree srcMethod) {
        Tree desired = TreeUtilFunctions.getParentUntilType(srcMethod, Constants.TYPE_DECLARATION);
        if (desired == null) return "";
        Tree nameNode = firstChildOfWithType(desired,Constants.SIMPLE_NAME);
        if (nameNode == null) return "";
        return nameNode.getLabel();
    }

    private Tree firstChildOfWithType(Tree desired, String type) {
        for (Tree child : desired.getChildren()) {
            if (child.getType().name.equals(type))
                return child;
        }
        return null;
    }
    public static boolean isStatement(String type){
        switch (type){
            case Constants.ASSERT_STATEMENT: //Leaf
            case Constants.BLOCK: // Composite
            case Constants.BREAK_STATEMENT: //Leaf
            case Constants.CONSTRUCTOR_INVOCATION: //leaf
            case Constants.CONTINUE_STATEMENT: //leaf
            case Constants.DO_STATEMENT: //composite
            case Constants.EMPTY_STATEMENT: //leaf
            case Constants.ENHANCED_FOR_STATEMENT: //composite
            case Constants.EXPRESSION_STATEMENT: //leaf
            case Constants.FOR_STATEMENT: //composite
            case Constants.IF_STATEMENT: //composite
            case Constants.LABELED_STATEMENT: //composite
            case Constants.RETURN_STATEMENT: //leaf
            case Constants.SUPER_CONSTRUCTOR_INVOCATION: //leaf
            case Constants.SWITCH_CASE: //leaf
            case Constants.SWITCH_STATEMENT: //composite
            case Constants.SYNCHRONIZED_STATEMENT: //composite
            case Constants.THROW_STATEMENT://leaf
            case Constants.TRY_STATEMENT: //composite
            case Constants.TYPE_DECLARATION_STATEMENT: //composite!!!!!!
            case Constants.VARIABLE_DECLARATION_STATEMENT: //leaf
            case Constants.WHILE_STATEMENT: //composite
                return true;
            default:
                return false;
        }   //Update the jdt version (website)
    }


    public void make() {
        result.addAll(makeForMethods());
    }

    public void write(String foldername) {
        //TODO: make files and ...
        ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter().withObjectIndenter(new DefaultIndenter("    ", "\n"));
        try {
            String out = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            out = prettify(out);
            FileUtils.writeStringToFile(new File(foldername + srcPath.replace(".java",".json")), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String prettify(String out) {
        return out;
    }
}

