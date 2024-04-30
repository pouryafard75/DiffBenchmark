package benchmark.oracle.generators.tools.runners;

import at.aau.softwaredynamics.matchers.MatcherFactory;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import shaded.com.github.gumtreediff.gen.TreeGenerator;
import shaded.com.github.gumtreediff.matchers.Mapping;
import shaded.com.github.gumtreediff.matchers.Matcher;
import shaded.com.github.gumtreediff.tree.ITree;

import java.io.IOException;

import static benchmark.oracle.generators.tools.Utils.findMirror;
import static benchmark.oracle.generators.tools.Utils.mirrorTree;

/* Created by pourya on 2023-04-17 8:10 p.m. */
public abstract class APIChanger {
    final ProjectASTDiff projectASTDiff;
    final ASTDiff rm_astDiff;
    APIChanger(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff){
        this.projectASTDiff = projectASTDiff;
        this.rm_astDiff = rm_astDiff;
    }
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
        String srcContents = projectASTDiff.getFileContentsBefore().get(rm_astDiff.getSrcPath());
        return getTreeRoot(getGeneratorType(), srcContents);
    }
    public ITree getDstRoot() throws IOException {
        String dstContents = projectASTDiff.getFileContentsAfter().get(rm_astDiff.getDstPath());
        return getTreeRoot(getGeneratorType(), dstContents);
    }
    public static ITree getTreeRoot(TreeGenerator gen, String srcContents) throws IOException {
        return gen.generateFromString(srcContents).getRoot();
    }

    public abstract Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType();
    public abstract shaded.com.github.gumtreediff.gen.TreeGenerator getGeneratorType();
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
        ASTDiff astDiff = diffToASTDiffWithActions(diff, this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath());
        return astDiff;
    }

    public static ASTDiff diffToASTDiffWithActions(Diff diff, String srcPath, String dstPath) {
        ASTDiff astDiff = diffToASTDiffNoAction(diff, srcPath, dstPath);
        astDiff.computeVanillaEditScript();
        return astDiff;
    }

    public static ASTDiff diffToASTDiffNoAction(Diff diff, String srcPath, String dstPath) {
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(diff.src.getRoot(), diff.dst.getRoot());
        mappings.add(diff.mappings);
        ASTDiff astDiff = new ASTDiff(srcPath, dstPath, diff.src, diff.dst, mappings);
        return astDiff;
    }

    public static ASTDiff makeASTDiffFromMatcher(CompositeMatchers.CompositeMatcher matcher, ASTDiff astDiff) {
        Tree srcRoot = astDiff.src.getRoot();
        Tree dstRoot = astDiff.dst.getRoot();
        srcRoot.setParent(null);
        dstRoot.setParent(null);
        MappingStore match = matcher.match(srcRoot, dstRoot);
        ExtendedMultiMappingStore mappingStore = new ExtendedMultiMappingStore(srcRoot, dstRoot);
        mappingStore.add(match);
        System.out.println(mappingStore.size());
        EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
        ASTDiff diff = new ASTDiff(astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.src, astDiff.dst, mappingStore, actions);
        if (diff.getAllMappings().size() != match.size())
            if (!astDiff.getSrcPath().equals("src_java_org_apache_commons_lang_math_NumberUtils.java"))
                throw new RuntimeException("Mapping has been lost!");
        return diff;
    }

    ASTDiff diffWithTrivialAddition(CaseInfo info, Configuration configuration) throws Exception {
        Diff diff = this.diff();
        ASTDiff astDiff = diffToASTDiffNoAction(diff, this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath());
        ExtendedMultiMappingStore trv = new TrivialDiff(this.projectASTDiff, this.rm_astDiff, info, configuration).makeASTDiff().getAllMappings();
        for (com.github.gumtreediff.matchers.Mapping mapping : trv) {
            astDiff.getAllMappings().addMapping(mapping.first, mapping.second);
        }
        return astDiff;
    }
}