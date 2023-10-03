package benchmark.metrics.models;

/* Created by pourya on 2023-04-16 2:42 a.m. */
public class DiffStats {
    private final Stats programElementStats;
    private final Stats abstractMappingStats;
    public DiffStats(Stats programElementStats, Stats abstractMappingStats) {
        this.programElementStats = programElementStats;
        this.abstractMappingStats = abstractMappingStats;
    }

    public Stats getProgramElementStats() {
        return programElementStats;
    }

    public Stats getAbstractMappingStats() {
        return abstractMappingStats;
    }
}
