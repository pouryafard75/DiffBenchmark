package benchmark.Oracle;

import java.util.List;

/* Created by pourya on 2023-04-03 4:47 a.m. */
public class DiffStats {
    private final List<Stats> abstractMapping_stats;
    private final List<Stats> programElement_stats;

    public DiffStats(List<Stats> abstractMapping_stats, List<Stats> programElement_stats) {
        this.abstractMapping_stats = abstractMapping_stats;
        this.programElement_stats = programElement_stats;
    }

    public List<Stats> getAbstractMapping_stats() {
        return abstractMapping_stats;
    }

    public List<Stats> getProgramElement_stats() {
        return programElement_stats;
    }
}
