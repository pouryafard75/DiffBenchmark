package benchmark.oracle.generators.changeAPI;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import benchmark.oracle.generators.APIChangerException;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
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
    private final ASTDiff rm_astDiff;
    APIChanger(ASTDiff rm_astDiff){
        this.rm_astDiff = rm_astDiff;
    }
    public Matcher makeMappings() throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        String srcContents = this.rm_astDiff.getSrcContents();
        String dstContents = this.rm_astDiff.getDstContents();
        ITree srcTC = gen.generateFromString(srcContents).getRoot();
        ITree dstTC = gen.generateFromString(dstContents).getRoot();
        shaded.com.github.gumtreediff.matchers.Matcher m = new MatcherFactory(getMatcherType()).createMatcher(srcTC, dstTC);
        m.match();
        return m;
//        return Utils.convert(m.getMappingSet(), srcContents, dstContents);
    }

    public abstract Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType();
    public Diff diff() throws Exception {
        Matcher m = makeMappings();
        Tree srcMirror = mirrorTree(m.getSrc());
        Tree dstMirror = mirrorTree(m.getDst());

        TreeContext srcTC = new TreeContext();
        srcTC.setRoot(srcMirror);
        TreeContext dstTC = new TreeContext();
        dstTC.setRoot(dstMirror);

        MappingStore mappingStore = new MappingStore(srcMirror,dstMirror);
        for (Mapping mapping : m.getMappings()) {
            mappingStore.addMapping(findMirror(mapping.first,srcMirror),findMirror(mapping.second,dstMirror));
        }
//        List<Action> iActions = new ActionGenerator(m.getSrc(), m.getDst(), m.getMappings()).generate();
//        EditScript editScript = new EditScript();
//        for (Action iAction : iActions)
//            editScript.add(mirrorAction(iAction,srcMirror,dstMirror));
        EditScript editScript = new SimplifiedChawatheScriptGenerator().computeActions(mappingStore);
        return new Diff(srcTC,dstTC,mappingStore,editScript);
    }
    public ASTDiff makeASTDiff() throws Exception {
        Diff diff = this.diff();
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(diff.src.getRoot(),diff.dst.getRoot());
        mappings.add(diff.mappings);
        return new ASTDiff(this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath(), diff.src, diff.dst, mappings);
    }
}
