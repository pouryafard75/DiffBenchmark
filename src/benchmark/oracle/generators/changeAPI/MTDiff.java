package benchmark.oracle.generators.changeAPI;

import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.IOException;

/* Created by pourya on 2023-04-17 7:59 p.m. */
public class MTDiff extends APIChanger {
    public MTDiff(ASTDiff astDiff) {
        super(astDiff);
    }
    @Override
    public Class<? extends shaded.com.github.gumtreediff.matchers.Matcher> getMatcherType() {
        return shaded.com.github.gumtreediff.matchers.OptimizedVersions.MtDiff.class;
    }
}
