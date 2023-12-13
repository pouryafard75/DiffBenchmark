package benchmark.metrics.computers.filters;


public enum MappingsLocationFilter {
    NO_FILTER(new NoFilter()),
    INTRA_FILE_ONLY(new NoInterFileFilter()),
    INTER_FILE_ONLY(new InterFileRelatedFilter()),
    MULTI_ONLY(new MultiMappingsFilter()); /* I know, This is not conceptually as same as the other enum value.
        Probably it was better to model this as boolean variable somewhere,
        but I couldn't help myself not to go with this solution.
        However, multi-mappings might be present in multiple locations
        thus it's not the worst decision i.m.o. . */

    private final HumanReadableDiffFilter filterForPerfect;

    MappingsLocationFilter(HumanReadableDiffFilter filterForPerfect) {
        this.filterForPerfect = filterForPerfect;
    }

    public HumanReadableDiffFilter getFilter() {
        return filterForPerfect;
    }
}

