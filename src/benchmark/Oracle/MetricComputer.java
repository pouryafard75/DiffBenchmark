package benchmark.Oracle;

import benchmark.AbstractMapping;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.matchers.Constants;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.util.List;

import static benchmark.OracleMaker.isStatement;

/* Created by pourya on 2023-04-03 3:53 a.m. */
public class MetricComputer {

    private final HumanReadableDiff godDiff;
    private final HumanReadableDiff toolDiff;
    private int numberOfIgnoredMappings;

    private int numberOfIgnoredElements;

    public MetricComputer(HumanReadableDiff godDiff, HumanReadableDiff toolDiff) {
        this.godDiff = godDiff;
        this.toolDiff = toolDiff;

    }

    private Stats makeStats(List<AbstractMapping> godList, List<AbstractMapping> toolList, int ignores) {
        int TP = 0;
        int FP = 0;
        int FN = 0;
        for (AbstractMapping mapping : godList) {
            if (toolList.contains(mapping))
                TP += 1;
            else
                FN += 1;
        }
        for (AbstractMapping mapping : toolList) {
            if (!godList.contains(mapping))
                FP += 1;
        }
        if (TP > ignores)
            TP = TP - ignores;
        return new Stats(TP, FP, FN);
    }

    public Stats programElementStats(){
        return makeStats(godDiff.matchedElements,toolDiff.matchedElements,numberOfIgnoredElements);
    }
    public Stats mappingStats(){
        return makeStats(godDiff.mappings,toolDiff.mappings,numberOfIgnoredMappings);
    }

    public void advance(Tree src, Tree dst, ExtendedMultiMappingStore mappings)
    {
        for (Mapping mapping : mappings) {
            if (mapping.first.getType().name.equals(Constants.METHOD_DECLARATION))
            {
                Tree srcFinalParent = TreeUtilFunctions.getParentUntilType(mapping.first, "CompilationUnit");
                Tree dstFinalParent = TreeUtilFunctions.getParentUntilType(mapping.second, "CompilationUnit");
                if (!srcFinalParent.equals(src) || !dstFinalParent.equals(dst)) continue;
                if (mapping.first.isIsomorphicTo(mapping.second))
                {
                    numberOfIgnoredElements += 1;
                    List<Tree> descendants = mapping.first.getDescendants();
                    for (Tree descendant : descendants) {
                        if (isStatement(descendant.getType().name))
                            numberOfIgnoredMappings += 1;
                    }
                }
            }
        }
        System.out.println();
    }
}

