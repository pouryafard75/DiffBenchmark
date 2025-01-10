package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.List;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.isComment;


/* Created by pourya on 2025-01-10*/
public class CommentCorrectorMappingOffsetTranslator extends MappingOffsetTranslator {

    public CommentCorrectorMappingOffsetTranslator(ASTDiff ref, ITranslationRuleProvider ruleProvider) {
        super(ref, ruleProvider);
    }

    @Override
    protected Tree investigate(Tree bad, Tree goodRoot) {
        //handle the discrepancy between the comment nodes
        List<Tree> sameStartCandidates = getCandidates(bad, goodRoot,
                (candidate, badNode) -> candidate.getPos() == badNode.getPos());
        List<Tree> sameEndCandidates = getCandidates(bad, goodRoot,
                (candidate, badNode) -> candidate.getEndPos() == badNode.getEndPos());
        if (!sameEndCandidates.isEmpty() && !sameStartCandidates.isEmpty()) {
            if (sameStartCandidates.size() == 1 && sameEndCandidates.size() == 1) {
                if (isComment(sameStartCandidates.getFirst().getType().name))
                    return sameEndCandidates.getFirst();
                if (isComment(sameEndCandidates.getFirst().getType().name))
                    return sameStartCandidates.getFirst();
            }
            else return null;
        }
        if (!sameStartCandidates.isEmpty())
            return chooseBestCandidate(bad, sameStartCandidates);
        if (!sameEndCandidates.isEmpty())
            return chooseBestCandidate(bad, sameEndCandidates);
        return null;
    }
}
