package benchmark;

import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.data.dataset.RefactoringOracleBenchmarkDataset;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.vanilla.MissingIdenticalNonAmbiguousSubtrees;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import java.util.Set;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.isBlock;
import static org.refactoringminer.astDiff.utils.TreeUtilFunctions.isStatement;

/* Created by pourya on 2025-02-06*/
public class exp {

    public static IBenchmarkDataset dataset = new RefactoringOracleBenchmarkDataset();
    public static void main(String[] args) throws Exception {
        for (IBenchmarkCase aCase : dataset.getCases()) {
//            if (!aCase.getID().equals("JacksonDatabind/44")) continue;
            for (ASTDiff rmDiff : aCase.getProjectASTDiff().getDiffSet()) {
                ASTDiff diff = ASTDiffToolEnum.GOD.diff(aCase, projectASTDiff -> rmDiff);
                Tree srcRoot = diff.src.getRoot();
                Tree dstRoot = diff.dst.getRoot();
                ExtendedMultiMappingStore mappingStore = new ExtendedMultiMappingStore(srcRoot, dstRoot);
                new MissingIdenticalNonAmbiguousSubtrees(
                        mapping -> isStatement(mapping.first.getType().name))
                        .match(srcRoot, dstRoot, mappingStore);
                for (Mapping mapping : mappingStore) {
                    if (isBlock(mapping.first.getType().name)) continue;
                    if (!isStatement(mapping.first.getType().name)) continue;
                    Set<Tree> dsts = diff.getAllMappings().getDsts(mapping.first);
                    if (dsts != null && !dsts.contains(mapping.second))
                    {
                        if (diff.getAllMappings().isSrcMapped(mapping.first) && diff.getAllMappings().isDstMapped(mapping.second)) continue;
//                        System.out.println("This is what we need, Pair of identical statements not in the ground-truth");
                        System.out.println(aCase.getID());
                        System.out.println(mapping.first.toTreeString());
                        System.out.println("--------------------------------------------------------------------------");
                    }
                }

            }

        }
    }

}
