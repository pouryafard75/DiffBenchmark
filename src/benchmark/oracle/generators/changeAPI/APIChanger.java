package benchmark.oracle.generators.changeAPI;

import at.aau.softwaredynamics.gen.OptimizedJdtTreeGenerator;
import at.aau.softwaredynamics.matchers.MatcherFactory;
import benchmark.oracle.generators.APIChangerException;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import shaded.com.github.gumtreediff.gen.jdt.AbstractJdtTreeGenerator;
import shaded.com.github.gumtreediff.tree.ITree;
import org.refactoringminer.astDiff.actions.ASTDiff;


import java.io.IOException;
import java.util.Set;

/* Created by pourya on 2023-04-17 8:10 p.m. */
public abstract class APIChanger {
    private final ASTDiff rm_astDiff;
    APIChanger(ASTDiff rm_astDiff){
        this.rm_astDiff = rm_astDiff;
    }
    public Set<shaded.com.github.gumtreediff.matchers.Mapping> makeMappings() throws IOException {
        AbstractJdtTreeGenerator gen = new OptimizedJdtTreeGenerator();
        String srcContents = this.rm_astDiff.getSrcContents();
        String dstContents = this.rm_astDiff.getDstContents();
        ITree srcTC = gen.generateFromString(srcContents).getRoot();
        ITree dstTC = gen.generateFromString(dstContents).getRoot();
        shaded.com.github.gumtreediff.matchers.Matcher m = new MatcherFactory(getMatcherType()).createMatcher(srcTC, dstTC);
        m.match();
        return m.getMappingSet();
//        return Utils.convert(m.getMappingSet(), srcContents, dstContents);
    }

    public abstract Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType();
    public Diff diff() throws IOException, APIChangerException {
        boolean _seen = false;
        EditScript actions;
        TreeContext dstTC;
        TreeContext srcTC;
        MappingStore mappingStore = null;
        Set<Mapping> convertedMappings = Utils.convert(makeMappings(), this.rm_astDiff.getSrcContents(), this.rm_astDiff.getDstContents());
        for (Mapping mapping : convertedMappings) {
            if (!_seen) {
                Tree srcRoot = TreeUtilFunctions.getParentUntilType(mapping.first, "CompilationUnit");
                Tree dstRoot = TreeUtilFunctions.getParentUntilType(mapping.second, "CompilationUnit");
                _seen = true;
                mappingStore = new MappingStore(srcRoot, dstRoot);
            }
            mappingStore.addMapping(mapping.first, mapping.second);
        }
        if (!_seen)
            mappingStore = new MappingStore(this.rm_astDiff.src.getRoot(), this.rm_astDiff.dst.getRoot());
        actions = new SimplifiedChawatheScriptGenerator().computeActions(mappingStore);
        srcTC = new TreeContext();
        srcTC.setRoot(mappingStore.src);
        dstTC = new TreeContext();
        dstTC.setRoot(mappingStore.dst);
        return new Diff(srcTC, dstTC, mappingStore, actions);
    }
    public ASTDiff makeASTDiff() throws IOException, APIChangerException {
            Diff diff = this.diff();
            ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(this.rm_astDiff.src.getRoot(), this.rm_astDiff.dst.getRoot());
            mappings.add(diff.mappings);
        return new ASTDiff(this.rm_astDiff.getSrcPath(), this.rm_astDiff.getDstPath(), this.rm_astDiff.src, this.rm_astDiff.dst,mappings);
    }
}
