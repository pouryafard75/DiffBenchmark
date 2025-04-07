package client;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.runners.extensions.labels.BlockAndSimpleNameModifier;
import benchmark.generators.tools.runners.extensions.labels.TreeModifier;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.io.TreeIoUtils;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import spoon.reflect.declaration.CtElement;

import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;

import static benchmark.generators.tools.runners.converter.Spoon.getCtPackageFromContent;

/* Created by pourya on 2025-02-01*/
public class Driver {
    public static void main(String[] args) throws Exception {
        x();
    }

    private static Diff fromFiles(String srcPath, String dstPath) throws IOException {
        TreeContext srcContext = TreeIoUtils.fromXml().generateFrom().file(srcPath);
        TreeContext dstContent = TreeIoUtils.fromXml().generateFrom().file(dstPath);
        Tree srcRoot = srcContext.getRoot();
        Tree dstRoot = dstContent.getRoot();
        MappingStore match = new CompositeMatchers.SimpleGumtree().match(srcRoot, dstRoot);
        EditScript editScript = (new SimplifiedChawatheScriptGenerator()).computeActions(match);
        Diff diff = new Diff(srcContext, dstContent, match, editScript);
        srcRoot.setParent(null);
        dstRoot.setParent(null);
        return diff;
    }

    private static void example() throws Exception {
        Diff diff = fromFiles("dot/src.xml", "dot/dst.xml");
        writeDot(diff, "dot/output.dot");
    }
    private static void fromRM() throws Exception {
        IBenchmarkCase aCase = new GithubCase("https://github.com/pouryafard75/TestCases/commit/28b4bfebe7afbc43d13798cc827c44c8a587e140");
        ASTDiff diff = ASTDiffToolEnum.RMD.diff(aCase, DiffSelector.any());
        writeDot(diff, "dot/comment.dot");
    }

    private static void writeDot(Diff diff, String file) throws Exception {
        DotDiff dotDiff = new DotDiff(diff);
        StringWriter stringWriter = dotDiff.getWriter();
        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write(stringWriter.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    static void x() throws Exception {
        String sourceCode = "class Test {\n" +
                "String foo(int i) {\n" +
                "   if (a <= b) \n" +
                "       return \"bar!\"; \n" +
                "return \"Foo!\";\n" +
                "}\n" +
                "}";

        TreeContext srcContext = new JdtTreeGenerator().generateFrom().string(sourceCode);
        System.out.println(srcContext.getRoot().toTreeString());

        shadedspoon.gumtree.spoon.builder.SpoonGumTreeBuilder scanner = new shadedspoon.gumtree.spoon.builder.SpoonGumTreeBuilder();
        CtElement leftCt = getCtPackageFromContent(sourceCode);
        shadedspoon.com.github.gumtreediff.tree.Tree tree = scanner.getTree(leftCt);
        System.out.println(tree.toTreeString());
        if (true) return;
        TreeContext dstContext = new TreeContext();
        TreeModifier treeModifier = new BlockAndSimpleNameModifier();
        Map<Tree, Tree> cpyMap = new LinkedHashMap<>();
        Tree dst = TreeUtilFunctions.deepCopyWithMap(srcContext.getRoot(), cpyMap);
        treeModifier.modify(dst);
        dstContext.setRoot(dst);
        MappingStore match = new MappingStore(srcContext.getRoot(), dstContext.getRoot());
        for (Map.Entry<Tree, Tree> treeTreeEntry : cpyMap.entrySet()) {
            match.addMapping(treeTreeEntry.getKey(), treeTreeEntry.getValue());
        }
        EditScript editScript = (new SimplifiedChawatheScriptGenerator()).computeActions(match);
        dstContext.setRoot(dst);
        Diff diff = new Diff(srcContext, dstContext, match, editScript);
        writeDot(diff, "dot/tree.dot");
    }

}

