package benchmark.gui;

import benchmark.OracleMaker;
import benchmark.gui.web.CompDiff;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import gui.utils.DiffUtils;
import gui.utils.URLHelper;
import gui.webdiff.WebDiff;
import org.refactoringminer.api.RefactoringMinerTimedOutException;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2022-12-26 9:30 p.m. */
public class CompareWithGitHubAPI {
    public static void main(String[] args) throws RefactoringMinerTimedOutException, IOException {
        String url = "https://github.com/pouryafard75/TestCases/commit/1be0f7e80fd1bf7c47e2e679d2397bcca0d0b30b";
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

            match = new CompositeMatchers.SimpleGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot());
            actions = new SimplifiedChawatheScriptGenerator().computeActions(match);
            diff = new Diff(astDiff.src, astDiff.dst, match, actions);
            GTS_astDiff.add(diff);

            MappingStore ijmMappings = new MappingStore(astDiff.src.getRoot(),astDiff.dst.getRoot());
            for (Mapping mapping : DiffUtils.IJMDiff(astDiff.getSrcContents(), astDiff.getDstContents())) {
                ijmMappings.addMapping(mapping.first,mapping.second);
            }
            actions = new SimplifiedChawatheScriptGenerator().computeActions(ijmMappings);
            IJM_astDiff.add(new Diff(astDiff.src, astDiff.dst, ijmMappings, actions));

            MappingStore mtdiffMapping = new MappingStore(astDiff.src.getRoot(),astDiff.dst.getRoot());
            for (Mapping mapping : DiffUtils.MTDiff(astDiff.getSrcContents(), astDiff.getDstContents())) {
                mtdiffMapping.addMapping(mapping.first,mapping.second);
            }
            actions = new SimplifiedChawatheScriptGenerator().computeActions(mtdiffMapping);
            MTD_astDiff.add(new Diff(astDiff.src, astDiff.dst, mtdiffMapping, actions));
        }
        new CompDiff(RM_astDiff,GTG_astDiff,GTS_astDiff,IJM_astDiff,MTD_astDiff).run();
    }
}
