package benchmark.generators.tools.runners.shaded;

import at.aau.softwaredynamics.matchers.MatcherFactory;
import benchmark.generators.tools.runners.trivial.TrivialDiff;
import benchmark.generators.tools.models.ASTDiffProviderFromProjectASTDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import shaded.com.github.gumtreediff.gen.TreeGenerator;
import shaded.com.github.gumtreediff.matchers.Mapping;
import shaded.com.github.gumtreediff.matchers.Matcher;
import shaded.com.github.gumtreediff.tree.ITree;

import java.io.IOException;

import static benchmark.generators.tools.runners.Utils.findMirror;
import static benchmark.generators.tools.runners.Utils.mirrorTree;

/* Created by pourya on 2023-04-17 8:10 p.m. */
public abstract class AbstractASTDiffProviderFromIncompatibleTree extends ASTDiffProviderFromProjectASTDiff {
    protected AbstractASTDiffProviderFromIncompatibleTree(ProjectASTDiff projectASTDiff, ASTDiff rmAstDiff) {
        super(projectASTDiff, rmAstDiff);
    }

    public AbstractASTDiffProviderFromIncompatibleTree(ProjectASTDiff projectASTDiff, ASTDiff input, CaseInfo info, Configuration conf) {
        super(projectASTDiff, input, info, conf);
    }

    public abstract Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType();
    public abstract shaded.com.github.gumtreediff.gen.TreeGenerator getGeneratorType();

    public Matcher makeMappings() throws IOException {
        shaded.com.github.gumtreediff.matchers.Matcher m = new MatcherFactory(
                getMatcherType()).createMatcher(
                getSrcRoot(),
                getDstRoot());
        m.match();
        return m;
//        return Utils.convert(m.getMappingSet(), srcContents, dstContents);
    }

    public ITree getSrcRoot() throws IOException {
        String srcContents = projectASTDiff.getFileContentsBefore().get(input.getSrcPath());
        return getTreeRoot(getGeneratorType(), srcContents);
    }
    public ITree getDstRoot() throws IOException {
        String dstContents = projectASTDiff.getFileContentsAfter().get(input.getDstPath());
        return getTreeRoot(getGeneratorType(), dstContents);
    }
    public static ITree getTreeRoot(TreeGenerator gen, String srcContents) throws IOException {
        return gen.generateFromString(srcContents).getRoot();
    }
    public Diff diff() throws Exception {
        Matcher m = makeMappings();
        return getDiff(m);
    }

    public static Diff getDiff(Matcher m) throws Exception {
        Tree srcMirror = mirrorTree(m.getSrc());
        Tree dstMirror = mirrorTree(m.getDst());

        TreeContext srcTC = new TreeContext();
        srcTC.setRoot(srcMirror);
        TreeContext dstTC = new TreeContext();
        dstTC.setRoot(dstMirror);
        MappingStore mappingStore = new MappingStore(srcMirror,dstMirror);
        EditScript editScript = new EditScript();
        try {
            for (Mapping mapping : m.getMappings()) {
                Tree decision = whichTree(m, srcMirror, dstMirror, mapping.first);
                Tree firstMirror = findMirror(mapping.first, decision);
                Tree decision2 = whichTree(m, srcMirror, dstMirror, mapping.second);
                Tree secondMirror = findMirror(mapping.second, decision2);
                mappingStore.addMapping(firstMirror, secondMirror);
            }
            editScript = new SimplifiedChawatheScriptGenerator().computeActions(mappingStore);
        }
        catch (Exception e)
        {
            throw new RuntimeException("Must check");
        }
        return new Diff(srcTC, dstTC, mappingStore, editScript);
    }

    private static Tree whichTree(Matcher m, Tree srcMirror, Tree dstMirror, ITree input) throws Exception {
        ITree tempParent = input;
        while (tempParent.getParent() != null)
            tempParent = tempParent.getParent();
        Tree decision;
        if (tempParent == m.getSrc())
            decision = srcMirror;
        else if (tempParent == m.getDst())
            decision = dstMirror;
        else
            throw new Exception("Must check");
        return decision;
    }

    public ASTDiff makeASTDiff() throws Exception {
        Diff diff = this.diff();
        return diffToASTDiffWithActions(diff, this.input.getSrcPath(), this.input.getDstPath());
    }

    public static ASTDiff diffToASTDiffWithActions(Diff diff, String srcPath, String dstPath) {
        ASTDiff astDiff = diffToASTDiffNoAction(diff, srcPath, dstPath);
        astDiff.computeVanillaEditScript();
        return astDiff;
    }

    public static ASTDiff diffToASTDiffNoAction(Diff diff, String srcPath, String dstPath) {
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(diff.src.getRoot(), diff.dst.getRoot());
        mappings.add(diff.mappings);
        return new ASTDiff(srcPath, dstPath, diff.src, diff.dst, mappings);
    }
    ASTDiff diffWithTrivialAddition(CaseInfo info, Configuration configuration) throws Exception {
        Diff diff = this.diff();
        ASTDiff astDiff = diffToASTDiffNoAction(diff, this.input.getSrcPath(), this.input.getDstPath());
        ExtendedMultiMappingStore trv = new TrivialDiff(this.projectASTDiff, this.input, info, configuration).makeASTDiff().getAllMappings();
        for (com.github.gumtreediff.matchers.Mapping mapping : trv) {
            astDiff.getAllMappings().addMapping(mapping.first, mapping.second);
        }
        astDiff.computeVanillaEditScript();
        return astDiff;
    }
}