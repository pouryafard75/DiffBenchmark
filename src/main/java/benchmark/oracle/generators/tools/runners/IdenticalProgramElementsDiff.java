package benchmark.oracle.generators.tools.runners;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.util.Set;
import java.util.function.Predicate;

import static benchmark.oracle.generators.diff.HumanReadableDiffGenerator.isProgramElement;

/* Created by pourya on 2023-04-16 5:07 a.m. */
