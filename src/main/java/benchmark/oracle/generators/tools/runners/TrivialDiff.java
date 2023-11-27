package benchmark.oracle.generators.tools.runners;

import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.MultiMappingStore;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/* Created by pourya on 2023-04-16 5:07 a.m. */
public class TrivialDiff {
    private final ASTDiff rm_astDiff;
    public ASTDiff makeASTDiff() {
        MappingStore mappings = new GreedySubtreeMatcher() {
            @Override
            public void filterMappings(MultiMappingStore multiMappings) {
                Set<Tree> ignored = new HashSet<>();
                for (var src : multiMappings.allMappedSrcs()) {
                    var isMappingUnique = false;
                    if (multiMappings.isSrcUnique(src)) {
                        var dst = multiMappings.getDsts(src).stream().findAny().get();
                        if (multiMappings.isDstUnique(dst)) {
                            if (isAcceptable(src, dst))
                                mappings.addMappingRecursively(src, dst);
                        }
                    }
                }
            }

            @Override
            protected void retainBestMapping(List<Mapping> mappingList, Set<Tree> srcIgnored, Set<Tree> dstIgnored) {
                //Do nothing in case of multiple candidates
            }
        }.match(rm_astDiff.src.getRoot(),rm_astDiff.dst.getRoot());
        ExtendedMultiMappingStore extendedMultiMappingStore = new ExtendedMultiMappingStore(rm_astDiff.src.getRoot(), rm_astDiff.dst.getRoot());
        extendedMultiMappingStore.add(mappings);
        return new ASTDiff(rm_astDiff.getSrcPath(),rm_astDiff.getDstPath(),rm_astDiff.src,rm_astDiff.dst, extendedMultiMappingStore);
    }

    public TrivialDiff(ASTDiff rm_astDiff){
        this.rm_astDiff = rm_astDiff;
    }

    private boolean isAcceptable(Tree src, Tree dst) {
        if (!src.getType().name.equals(dst.getType().name)) throw new RuntimeException("Types are not equal");
        return isProgramElement(src.getType().name);
    }

    private static boolean isProgramElement(String firstType) {
        switch (firstType) {
            case Constants.TYPE_DECLARATION:
            case Constants.METHOD_DECLARATION:
            case Constants.FIELD_DECLARATION:
            case Constants.ENUM_DECLARATION:
                return true;
            default:
                return false;
        }
    }
}
