package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.models.ASTDiffProviderFromProjectASTDiff;
import benchmark.models.selector.DiffSelector;
import com.github.gumtreediff.tree.DefaultTree;
import com.github.gumtreediff.tree.TypeSet;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import shadedspoon.com.github.gumtreediff.matchers.Mapping;
import shadedspoon.com.github.gumtreediff.tree.Tree;
import shadedspoon.gumtree.spoon.AstComparator;
import shadedspoon.gumtree.spoon.diff.DiffConfiguration;
import shadedspoon.gumtree.spoon.diff.DiffImpl;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.support.compiler.VirtualFile;

import static benchmark.generators.tools.runners.Utils.makeASTDiff;

/* Created by pourya on 2024-09-09*/
public class Spoon extends ASTDiffProviderFromProjectASTDiff {

    public final DiffConfiguration configuration;

    public Spoon(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
        super(benchmarkCase, querySelector);
        configuration = null; // It will be the default configuration of the Spoon
    }

    public Spoon(IBenchmarkCase benchmarkCase, DiffSelector querySelector, DiffConfiguration configuration) {
        super(benchmarkCase, querySelector);
        this.configuration = configuration;
    }

    @Override
    public ASTDiff getASTDiff() throws Exception {
        CtElement leftCt = getCtPackageFromContent(projectASTDiff.getFileContentsBefore().get(input.getSrcPath()));
        CtElement rightCt = getCtPackageFromContent(projectASTDiff.getFileContentsAfter().get(input.getDstPath()));
        shadedspoon.gumtree.spoon.builder.SpoonGumTreeBuilder scanner = new shadedspoon.gumtree.spoon.builder.SpoonGumTreeBuilder();
        DiffImpl diff = (configuration == null) ?
                        new DiffImpl(scanner.getTreeContext(),
                                scanner.getTree(leftCt),
                                scanner.getTree(rightCt)) :
                        new DiffImpl(scanner.getTreeContext(),
                                scanner.getTree(leftCt),
                                scanner.getTree(rightCt),
                                configuration);
        BiMap<Tree, com.github.gumtreediff.tree.Tree> srcBible = HashBiMap.create();
        BiMap<Tree, com.github.gumtreediff.tree.Tree> dstBible = HashBiMap.create();
        com.github.gumtreediff.tree.Tree src = unshaded(diff.getMappingsComp().src, srcBible);
        com.github.gumtreediff.tree.Tree dst = unshaded(diff.getMappingsComp().dst, dstBible);

        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(src, dst);
        populate(mappings, diff, src, dst, srcBible, dstBible);
        ASTDiff astDiff = makeASTDiff(input, src, dst, mappings);
        astDiff.computeVanillaEditScript();
        return astDiff;
    }

    private void populate(ExtendedMultiMappingStore mappings, DiffImpl diff, com.github.gumtreediff.tree.Tree src, com.github.gumtreediff.tree.Tree dst, BiMap<Tree, com.github.gumtreediff.tree.Tree> srcBible, BiMap<Tree, com.github.gumtreediff.tree.Tree> dstBible) {
        for (Mapping mapping : diff.getMappingsComp()) {
            mappings.addMapping(srcBible.get(mapping.first), dstBible.get(mapping.second));
        }
    }


    private com.github.gumtreediff.tree.Tree unshaded(Tree t, BiMap<Tree, com.github.gumtreediff.tree.Tree> bible) {
        DefaultTree curr = new DefaultTree(TypeSet.type(t.getType().name));
        curr.setLabel(t.getLabel());
        curr.setPos(t.getPos());
        // The reason behind the following refining of the offsets can be found
        // @ https://github.com/SpoonLabs/gumtree-spoon-ast-diff/issues/327#issuecomment-2369017420
        // Based on my own observation, some subtrees have [0,0] which I prefer not to modify,
        // At the moment I am not sure how to generalize this though ...
        int refinedLength = (t.getPos() == 0 && t.getLength() == 0) ?
                t.getLength() : t.getLength() + 1;
        curr.setLength(refinedLength);
        for (Tree child : t.getChildren()) {
            com.github.gumtreediff.tree.Tree childMirror = unshaded(child, bible);
            childMirror.setParent(curr);
            curr.addChild(childMirror);
        }
        bible.put(t, curr);
        return curr;
    }


    private static CtPackage getCtPackageFromContent(String content) {
        VirtualFile resource = new VirtualFile(content, getFilename(content));
        return new AstComparator().getCtPackage(resource);
    }
    private static String getFilename(String content) {
        return "test"+Math.abs(content.hashCode()) + ".java";
    }
}
