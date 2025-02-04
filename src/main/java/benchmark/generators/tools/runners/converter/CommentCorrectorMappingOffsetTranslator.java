package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.List;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.isComment;
import static benchmark.generators.hrd.HumanReadableDiffGenerator.isProgramElement;


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
            if (sameStartCandidates.size() == 1 && sameEndCandidates.size() == 1){
                if (isComment(sameStartCandidates.get(0).getType().name) && isComment(sameEndCandidates.get(0).getType().name)){
                    //program element is between the offsets
                    return selectProgramElement(goodRoot.getTreesBetweenPositions(bad.getPos(), bad.getEndPos()));
                }
            }
            if (sameStartCandidates.size() == 1){
                if (isComment(sameStartCandidates.get(0).getType().name))
                    return selectProgramElement(sameEndCandidates);
                else
                {
                    //Verified and it's alright
                }
            }
            else if (sameEndCandidates.size() == 1){
                if (isComment(sameEndCandidates.get(0).getType().name))
                    return selectProgramElement(sameStartCandidates);
                else
                {
                    //Verified and it's alright
                }
            }
        }

        if (!sameStartCandidates.isEmpty())
            return chooseBestCandidate(bad, sameStartCandidates);
        if (!sameEndCandidates.isEmpty())
            return chooseBestCandidate(bad, sameEndCandidates);
        return null;
    }

    private Tree selectProgramElement(List<Tree> sameEndCandidates) {
        return sameEndCandidates.stream().filter(candidate -> isProgramElement(candidate.getType().name)).findFirst().orElse(null);
    }
}
