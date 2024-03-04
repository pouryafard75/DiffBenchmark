package benchmark.oracle.generators.tools.runners;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.matchers.MultiMappingStore;
import com.github.gumtreediff.matchers.heuristic.gt.GreedySubtreeMatcher;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import static benchmark.oracle.generators.diff.HumanReadableDiffGenerator.isProgramElement;

/* Created by pourya on 2023-04-16 5:07 a.m. */
public class BaseTrivialDiff {
    private final ASTDiff rm_astDiff;
    private Predicate<Mapping> condition;

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
                            addMappingRecursively(mappings, src, dst);
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
        ASTDiff astDiff = new ASTDiff(rm_astDiff.getSrcPath(), rm_astDiff.getDstPath(), rm_astDiff.src, rm_astDiff.dst, extendedMultiMappingStore);
        astDiff.computeVanillaEditScript();
        return astDiff;
    }

    public void setCondition(Predicate<Mapping> condition) {
        this.condition = condition;
    }

    public BaseTrivialDiff(ASTDiff rm_astDiff){
        this.rm_astDiff = rm_astDiff;
    }

    public BaseTrivialDiff(ASTDiff rm_astDiff, Predicate<Mapping> condition){
        this.rm_astDiff = rm_astDiff;
        this.condition = condition;
    }

    private boolean isAcceptable(Tree src, Tree dst) {
        if (!src.getType().name.equals(dst.getType().name)) throw new RuntimeException("Types are not equal");
        return condition.test(new Mapping(src, dst));

    }

    public void addMappingRecursively(MappingStore m, Tree src, Tree dst) {
        if (isAcceptable(src, dst))
            m.addMappingRecursively(src, dst);
        else {
            for (int i = 0; i < src.getChildren().size(); ++i) {
                addMappingRecursively(m, src.getChild(i), dst.getChild(i));
            }
        }

    }
}
