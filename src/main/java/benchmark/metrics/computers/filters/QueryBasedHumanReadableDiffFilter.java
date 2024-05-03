package benchmark.metrics.computers.filters;

import benchmark.models.AbstractMapping;
import benchmark.models.HumanReadableDiff;
import benchmark.models.NecessaryMappings;
import gr.uom.java.xmi.diff.CodeRange;

import java.util.Collection;
import java.util.HashSet;
import java.util.function.Function;

/* Created by pourya on 2023-11-30 1:31 a.m. */
public class QueryBasedHumanReadableDiffFilter implements HumanReadableDiffFilter {
    private final Collection<CodeRange> leftRanges;
    private final Collection<CodeRange> rightRanges;

    public QueryBasedHumanReadableDiffFilter(Collection<CodeRange> leftRanges, Collection<CodeRange> rightRanges) {
        this.leftRanges = leftRanges;
        this.rightRanges = rightRanges;
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
        Collection<AbstractMapping> collection = criteriaSelector.apply(original.getIntraFileMappings());
        Collection<AbstractMapping> lefts = getCriteriaBasedOnOffsetFunctions(leftRanges, AbstractMapping::getLeftOffset, AbstractMapping::getLeftEndOffset, collection);
        Collection<AbstractMapping> rights = getCriteriaBasedOnOffsetFunctions(rightRanges, AbstractMapping::getRightOffset, AbstractMapping::getRightEndOffset, collection);

        Collection<AbstractMapping> intersection = intersection(lefts, rights);
        Collection<AbstractMapping> result = new HashSet<>(intersection);
        unionOfDeltas(lefts, rights)
                .stream()
                .filter(abstractMapping -> notMulti(abstractMapping, intersection))
                .forEach(result::add);
        return result;

    }

    private boolean notMulti(AbstractMapping abstractMapping, Collection<AbstractMapping> intersection) {
        long count = intersection
                .stream()
                .filter(mapping ->
                        mapping.getLeftInfo().equals(abstractMapping.getLeftInfo())
                                ||
                        mapping.getRightInfo().equals(abstractMapping.getRightInfo()))
                .count();
        return count == 0;
    }

    private static Collection<AbstractMapping> intersection(Collection<AbstractMapping> lefts, Collection<AbstractMapping> rights) {
        Collection<AbstractMapping> result = new HashSet<>(lefts);
        result.retainAll(rights);
        return result;
    }

    private static Collection<AbstractMapping> union(Collection<AbstractMapping> left, Collection<AbstractMapping> right){
        Collection<AbstractMapping> result = new HashSet<>(left);
        result.addAll(right);
        return result;
    }

    private static Collection<AbstractMapping> unionOfDeltas(Collection<AbstractMapping> left, Collection<AbstractMapping> right) {
        Collection<AbstractMapping> unionOfDeltas = new HashSet<>(union(left, right));
        unionOfDeltas.removeAll(intersection(left, right));
        return unionOfDeltas;
    }


    private static Collection<AbstractMapping> getCriteriaBasedOnOffsetFunctions(Collection<CodeRange> rangeSelector, Function<AbstractMapping, Integer> startOffsetFunction, Function<AbstractMapping, Integer> endOffsetFunction, Collection<AbstractMapping> collection) {
        Collection<AbstractMapping> result = new HashSet<>();
        for (CodeRange range : rangeSelector) {
//            if (!fileNameAsFolder(leftRange.getFilePath()).equals(path)) continue;
            collection.forEach(abstractMapping -> {
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
