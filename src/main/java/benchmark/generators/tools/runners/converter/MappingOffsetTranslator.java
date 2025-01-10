package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.isBetweenDifferentTypes;

/* Created by pourya on 2024-10-07*/
public class MappingOffsetTranslator extends AbstractOffsetTranslator {
    private final ITranslationRuleProvider ruleProvider;
    public MappingOffsetTranslator(ASTDiff ref, ITranslationRuleProvider ruleProvider) {
        super(ref);
        this.ruleProvider = ruleProvider;
    }
    public MappingOffsetTranslator(ASTDiff ref) {
        this(ref, List::of);
    }

    public ASTDiff translate(ASTDiff foreign) {
        ExtendedMultiMappingStore mappings = new ExtendedMultiMappingStore(
                reference.src.getRoot(),
                reference.dst.getRoot()
        );
        for (Mapping mapping : foreign.getAllMappings()) {
            Tree srcSub = findEqv(mapping.first, reference.src.getRoot());
            Tree dstSub = findEqv(mapping.second, reference.dst.getRoot());
            if (srcSub != null && dstSub != null) {
                Mapping translated = new Mapping(srcSub, dstSub);
                if (!isBetweenDifferentTypes(translated)) {
                    mappings.addMapping(translated.first, translated.second);
                    ruleProvider.getRules().forEach(rule -> rule.accept(mapping, translated, mappings));
                }
                else
                    System.out.println("Mapping between different types: " + translated);

            }
            else {
//                System.out.println("Mapping not found: " + mapping);
            }
        }
        //Ensure the roots are mapped as well.
        mappings.addMapping(reference.src.getRoot(), reference.dst.getRoot());
        ASTDiff translated = new ASTDiff(reference.getSrcPath(), reference.getDstPath(), reference.src, reference.dst, mappings);
//        translated.computeVanillaEditScript(); //leave the editscript calculation to the caller
        return translated;
    }

    protected Tree findEqv(Tree bad, Tree goodRoot) {
        if (bad.getPos() == 0 && bad.getEndPos() == 0)
            return null;
        List<Tree> candidates = getCandidates(bad, goodRoot,
                (candidate, badNode) -> candidate.getPos() == badNode.getPos(),
                (candidate, badNode) -> candidate.getEndPos() == badNode.getEndPos()
        );
        if (candidates.isEmpty())
            return investigate(bad, goodRoot);
        return chooseBestCandidate(bad, candidates);
    }

    @SafeVarargs
    protected static List<Tree> getCandidates(Tree bad, Tree goodRoot, BiPredicate<Tree, Tree>... predicates) {
        List<Tree> candidates = new ArrayList<>();
        for (Tree candidate : goodRoot.preOrder()) {
            boolean satisfiedAll = true;
            for (BiPredicate<Tree, Tree> predicate : predicates) {
                if (!predicate.test(bad, candidate))
                {
                    satisfiedAll = false;
                    break;
                }
            }
            if (satisfiedAll)
                candidates.add(candidate);
        }
        return candidates;
    }

    protected Tree investigate(Tree bad, Tree goodRoot) {
        return null;
    }

    protected Tree chooseBestCandidate(Tree bad, List<Tree> candidates) {
        if (candidates.isEmpty()) return null;
        if (candidates.size() == 1) {
            return candidates.get(0);
        }
        else {
            for (Tree candidate : candidates) {
                if (candidate.isLeaf())
                    return candidate;
            }
        }
        return mostInner(candidates);
    }

    private Tree mostInner(List<Tree> candidates) {
        Tree mostInner = candidates.get(0);
        for (Tree candidate : candidates) {
            if (candidate.getMetrics().depth > mostInner.getMetrics().depth)
                mostInner = candidate;
        }
        return mostInner;
    }
}
