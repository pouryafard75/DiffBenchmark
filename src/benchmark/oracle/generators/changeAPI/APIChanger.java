package benchmark.oracle.generators.changeAPI;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.tree.ITree;

import java.io.IOException;
import java.util.Set;

import static benchmark.oracle.generators.changeAPI.Utils.convert;

/* Created by pourya on 2023-04-17 8:10 p.m. */
public abstract class APIChanger {
    private final ASTDiff astDiff;
    APIChanger(ASTDiff astDiff){
        this.astDiff = astDiff;
    }

    public ASTDiff getAstDiff() {
        return astDiff;
    }
    public Set<Mapping> makeMappings() throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        String srcContents = astDiff.getSrcContents();
        String dstContents = astDiff.getDstContents();
        ITree srcTC = gen.generateFromString(srcContents).getRoot();
        ITree dstTC = gen.generateFromString(dstContents).getRoot();
        shaded.com.github.gumtreediff.matchers.Matcher m = new MatcherFactory(getMatcherType()).createMatcher(srcTC, dstTC);
        m.match();
        return convert(m.getMappingSet(), srcContents, dstContents);
    }

    public abstract Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType();
    public Diff diff() throws IOException {
        boolean _seen = false;
        EditScript actions;
        TreeContext dstTC;
        TreeContext srcTC;
        MappingStore mappingStore = null;
        for (Mapping mapping : makeMappings()) {
            if (!_seen) {
                Tree srcRoot = TreeUtilFunctions.getParentUntilType(mapping.first, "CompilationUnit");
                Tree dstRoot = TreeUtilFunctions.getParentUntilType(mapping.second, "CompilationUnit");
                _seen = true;
                mappingStore = new MappingStore(srcRoot, dstRoot);
            }
            mappingStore.addMapping(mapping.first, mapping.second);
        }
        if (!_seen)
            mappingStore = new MappingStore(getAstDiff().src.getRoot(), getAstDiff().dst.getRoot());
        actions = new SimplifiedChawatheScriptGenerator().computeActions(mappingStore);
        srcTC = new TreeContext();
        srcTC.setRoot(mappingStore.src);
        dstTC = new TreeContext();
        dstTC.setRoot(mappingStore.dst);
        return new Diff(srcTC, dstTC, mappingStore, actions);
    }
}
