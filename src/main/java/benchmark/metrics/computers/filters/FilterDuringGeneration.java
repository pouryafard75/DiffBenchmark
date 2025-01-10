package benchmark.metrics.computers.filters;


import benchmark.models.hrd.HumanReadableDiff;
import org.refactoringminer.astDiff.utils.Constants;

import static benchmark.metrics.computers.filters.FilterUtils.getFilterByType;


public enum FilterDuringGeneration implements HumanReadableDiffFilter{
    NO_FILTER(new NoFilter()),
    INTRA_FILE_ONLY(new NoInterFileFilter()),
    INTER_FILE_ONLY(new InterFileRelatedFilter()),
    MULTI_ONLY(new MultiMappingsFilter()), /* I know, This is not conceptually as same as the other enum value.
        Probably it was better to model this as boolean variable somewhere,
        but I couldn't help myself not to go with this solution.
        However, multi-mappings might be present in multiple locations
        thus it's not the worst decision i.m.o. . */

    COMMENTS_ONLY(HumanReadableDiffFilter.byPredicate(
            getFilterByType(Constants.LINE_COMMENT).or(
                    getFilterByType(Constants.BLOCK_COMMENT))
    )),
    ;

    private final HumanReadableDiffFilter filterForPerfect;

    FilterDuringGeneration(HumanReadableDiffFilter filterForPerfect) {
        this.filterForPerfect = filterForPerfect;
    }

    public HumanReadableDiffFilter getFilter() {
        return filterForPerfect;
    }

    @Override
    public HumanReadableDiff make(HumanReadableDiff original, HumanReadableDiff slack) {
        return filterForPerfect.make(original, slack);
    }
}

