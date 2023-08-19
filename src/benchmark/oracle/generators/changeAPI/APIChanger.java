package benchmark.oracle.generators.changeAPI;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.matchers.Mapping;
import shaded.com.github.gumtreediff.matchers.Matcher;
import shaded.com.github.gumtreediff.tree.ITree;

import java.io.IOException;

import static benchmark.oracle.generators.changeAPI.Utils.findMirror;
import static benchmark.oracle.generators.changeAPI.Utils.mirrorTree;

/* Created by pourya on 2023-04-17 8:10 p.m. */
public abstract class APIChanger {
    private final ProjectASTDiff projectASTDiff;
    private final ASTDiff rm_astDiff;
    APIChanger(ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff){
        this.projectASTDiff = projectASTDiff;
        this.rm_astDiff = rm_astDiff;
    }
    public Matcher makeMappings() throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        String srcContents = projectASTDiff.getFileContentsBefore().get(rm_astDiff.getSrcPath());
        String dstContents = projectASTDiff.getFileContentsAfter().get(rm_astDiff.getDstPath());
        ITree srcITree = gen.generateFromString(srcContents).getRoot();
        ITree dstITree = gen.generateFromString(dstContents).getRoot();
        shaded.com.github.gumtreediff.matchers.Matcher m = new MatcherFactory(getMatcherType()).createMatcher(srcITree, dstITree);
        m.match();
        return m;
//        return Utils.convert(m.getMappingSet(), srcContents, dstContents);
    }

    public abstract Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType();
    public shaded.com.github.gumtreediff.gen.TreeGenerator getGeneratorType(){
        return new OptimizedJdtTreeGenerator();
    }
    public Diff diff() throws Exception {
        Matcher m = makeMappings();
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
            System.err.println("Check this !!");
            e.printStackTrace();
        }
        return new Diff(srcTC,dstTC,mappingStore,editScript);
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
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(diff.src.getRoot(),diff.dst.getRoot());
        mappings.add(diff.mappings);
        ASTDiff astDiff = new ASTDiff(this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath(), diff.src, diff.dst, mappings);
        return astDiff;
    }
}
