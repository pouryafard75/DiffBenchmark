package benchmark.oracle.generators;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.github.gumtreediff.actions.model.Action;
import com.github.gumtreediff.actions.model.TreeAddition;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.actions.model.MoveIn;
import org.refactoringminer.astDiff.actions.model.MoveOut;
import org.refactoringminer.astDiff.matchers.Constants;

import java.util.Map;

import static benchmark.oracle.generators.GeneratorUtils.*;
import static benchmark.oracle.generators.GeneratorUtils.generateFieldSignature;
import static benchmark.oracle.generators.HumanReadableDiffGenerator.*;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.isStatement;

/* Created by pourya on 2023-09-15 4:26 p.m. */
public class HRDGen2 extends HumanReadableDiffGenerator {
    public HRDGen2(ProjectASTDiff projectASTDiff, ASTDiff generated, CaseInfo info, Configuration configuration) {
        super(projectASTDiff, generated, info, configuration);
    }

    @Override
    void make() {
        throw new RuntimeException("Not implemented");
    }
}
