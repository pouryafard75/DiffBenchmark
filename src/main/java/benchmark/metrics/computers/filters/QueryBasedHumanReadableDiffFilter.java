package benchmark.metrics.computers.filters;

import benchmark.oracle.models.AbstractMapping;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.oracle.models.NecessaryMappings;
import gr.uom.java.xmi.diff.CodeRange;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

/* Created by pourya on 2023-11-30 1:31 a.m. */
public class QueryBasedHumanReadableDiffFilter implements HumanReadableDiffFilter {
    private final Collection<CodeRange> leftRanges;
    private final Collection<CodeRange> rightRanges;

    public QueryBasedHumanReadableDiffFilter(Collection<CodeRange> leftRanges, Collection<CodeRange> rightRanges) {
        this.leftRanges = leftRanges;
        this.rightRanges = rightRanges;
    }
    @Override
    public HumanReadableDiff make(HumanReadableDiff original) {
        return make(original, original);
    }

    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        HumanReadableDiff result = HumanReadableDiff.makeEmpty();
        Collection<AbstractMapping> matchedElements = getLeftAndRightBasedOnSelector(original, NecessaryMappings::getMatchedElements);
        Collection<AbstractMapping> mappings = getLeftAndRightBasedOnSelector(original, NecessaryMappings::getMappings);
        NecessaryMappings intraFile = new NecessaryMappings(matchedElements, mappings);
        result.setIntraFileMappings(intraFile);
        return result;
    }

    private Collection<AbstractMapping> getLeftAndRightBasedOnSelector(HumanReadableDiff original, Function<NecessaryMappings, Collection<AbstractMapping>> criteriaSelector) {
        Collection<AbstractMapping> result = new HashSet<>();
        result.addAll(getCriteriaBasedOnOffsetFunctions(original, leftRanges, AbstractMapping::getLeftOffset, AbstractMapping::getLeftEndOffset, criteriaSelector));
        result.addAll(getCriteriaBasedOnOffsetFunctions(original, rightRanges,AbstractMapping::getRightOffset, AbstractMapping::getRightEndOffset, criteriaSelector));
        return result;
    }

    private static Collection<AbstractMapping> getCriteriaBasedOnOffsetFunctions(HumanReadableDiff original, Collection<CodeRange> rangeSelector, Function<AbstractMapping, Integer> startOffsetFunction, Function<AbstractMapping, Integer> endOffsetFunction, Function<NecessaryMappings, Collection<AbstractMapping>> criteriaSelector) {
        Collection<AbstractMapping> result = new HashSet<>();
        for (CodeRange range : rangeSelector) {
//            if (!fileNameAsFolder(leftRange.getFilePath()).equals(path)) continue;
            criteriaSelector.apply(original.getIntraFileMappings()).forEach(abstractMapping -> {
                if (
                        subsumes(range.getStartOffset(),
                        range.getEndOffset(),
                        startOffsetFunction.apply(abstractMapping),
                        endOffsetFunction.apply(abstractMapping))
                ) {
                    result.add(abstractMapping);
                }
            });
        }
        return result;
    }
    private static boolean subsumes(int rangeStart, int rangeEnd, int offset, int leftEndOffset) {
        return rangeStart <= offset && rangeEnd >= leftEndOffset;
    }
}
