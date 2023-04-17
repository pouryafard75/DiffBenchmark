package benchmark.gui;

import benchmark.gui.web.BenchmarkDiff;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import gui.utils.DiffUtils;
import gui.utils.URLHelper;
import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static gui.utils.DiffUtils.changeAPI;
import static gui.utils.DiffUtils.myMethod;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws Exception {
        String url = "https://github.com/spring-projects/spring-boot/commit/cb98ee25ff52bf97faebe3f45cdef0ced9b4416e";
        String repo = URLHelper.getRepo(url);
        String commit = URLHelper.getCommit(url);

        Set<ASTDiff> RM_astDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 1000);
        Set<Diff> GTG_astDiff = new LinkedHashSet<>();
        Set<Diff> GTS_astDiff = new LinkedHashSet<>();
        Set<Diff> IJM_astDiff = new LinkedHashSet<>();
        Set<Diff> MTD_astDiff = new LinkedHashSet<>();
        for (ASTDiff astDiff : RM_astDiff) {
            MappingStore match = new CompositeMatchers.ClassicGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot());
            EditScript actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
            Diff diff = new Diff(astDiff.src, astDiff.dst, match, actions);
            GTG_astDiff.add(diff);
            //
            match = new CompositeMatchers.SimpleGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot());
            actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
            diff = new Diff(astDiff.src, astDiff.dst, match, actions);
            GTS_astDiff.add(diff);

            //TODO: Fix the issue with IJM & MTDiff
            //
//            MappingStore ijmMappings = null;
//            boolean _seen = false;
//            for (Mapping mapping : DiffUtils.IJMDiff(astDiff.getSrcContents(), astDiff.getDstContents())) {
//                if (!_seen)
//                {
//                    Tree srcRoot = TreeUtilFunctions.getParentUntilType(mapping.first, "CompilationUnit");
//                    Tree dstRoot = TreeUtilFunctions.getParentUntilType(mapping.second, "CompilationUnit");
//                    _seen = true;
//                    ijmMappings = new MappingStore(srcRoot,dstRoot);
//                }
//                ijmMappings.addMapping(mapping.first,mapping.second);
//            }
//            if (!_seen)
//                ijmMappings = new MappingStore(astDiff.src.getRoot(),astDiff.dst.getRoot());
//            actions = new SimplifiedChawatheScriptGenerator().computeActions(ijmMappings);
//            EditScript modifiedEditScript = new EditScript();
//            for (Action action : actions) {
//                if (!action.getNode().getType().name.equals(Constants.SIMPLE_NAME))
//                    modifiedEditScript.add(action);
//            }
//            TreeContext srcTC = new TreeContext();
//            srcTC.setRoot(ijmMappings.src);
//            TreeContext dstTC = new TreeContext();
//            dstTC.setRoot(ijmMappings.dst);
//            IJM_astDiff.add(new Diff(srcTC,dstTC,ijmMappings, modifiedEditScript));
//            //
//            MappingStore mtdiffmappings = null;
//            _seen = false;
//            for (Mapping mapping : DiffUtils.MTDiff(astDiff.getSrcContents(), astDiff.getDstContents())) {
//                if (!_seen)
//                {
//                    Tree srcRoot = TreeUtilFunctions.getParentUntilType(mapping.first, "CompilationUnit");
//                    Tree dstRoot = TreeUtilFunctions.getParentUntilType(mapping.second, "CompilationUnit");
//                    _seen = true;
//                    mtdiffmappings = new MappingStore(srcRoot,dstRoot);
//                }
//                mtdiffmappings.addMapping(mapping.first,mapping.second);
//            }
//            if (!_seen)
//                mtdiffmappings = new MappingStore(astDiff.src.getRoot(),astDiff.dst.getRoot());
//            actions = new SimplifiedChawatheScriptGenerator().computeActions(mtdiffmappings);
//            srcTC = new TreeContext();
//            srcTC.setRoot(mtdiffmappings.src);
//            dstTC = new TreeContext();
//            dstTC.setRoot(mtdiffmappings.dst);
//            MTD_astDiff.add(new Diff(srcTC,dstTC,mtdiffmappings, actions));
            //
        }
        new BenchmarkDiff(RM_astDiff,GTG_astDiff,GTS_astDiff,IJM_astDiff,MTD_astDiff).run();

    }
}
