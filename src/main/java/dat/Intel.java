package dat;

import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.Stats;
import benchmark.oracle.models.NecessaryMappings;

/* Created by pourya on 2024-01-22*/
public class Intel {

    final String repo, commit , srcPath;
    final String matcher,  conf;
    final int edSize, edSizeNonJavaDoc;

    final int TRV_mappings, TRV_programElements;

    final int TP_raw_mappings, TP_mappings,  FP_mappings, FN_mappings;
    final int TP_raw_programElements, TP_programElements, FP_programElements, FN_programElements;

    final double precision; final double recall; final double f1;

    public Intel(String repo, String commit, String srcPath,
                 String matcher, String conf,
                 int edSize, int edSizeNonJavaDoc,
                 NecessaryMappings ignore,
                 DiffStats mappingsStats) {
        this.repo = repo; this.commit = commit; this.srcPath = srcPath;
        this.matcher = matcher; this.conf = conf;
        this.edSize = edSize; this.edSizeNonJavaDoc = edSizeNonJavaDoc;

        this.TRV_mappings = ignore.getMappings().size(); this.TRV_programElements = ignore.getMatchedElements().size();

        this.TP_raw_mappings = mappingsStats.getAbstractMappingStats().getTP();
        this.TP_mappings = Math.max(TP_raw_mappings - TRV_mappings, 0);
        this.FP_mappings = mappingsStats.getAbstractMappingStats().getFP();
        this.FN_mappings = mappingsStats.getAbstractMappingStats().getFN();

        this.TP_raw_programElements = mappingsStats.getProgramElementStats().getTP();
        this.TP_programElements = Math.max(TP_raw_programElements - TRV_programElements , 0);
        this.FP_programElements = mappingsStats.getProgramElementStats().getFP();
        this.FN_programElements = mappingsStats.getProgramElementStats().getFN();

        Stats effective_stats = new Stats(TP_mappings + TP_programElements, FP_mappings + FP_programElements, FN_mappings + FN_programElements);
        this.f1 = effective_stats.calcF1();
        this.precision = effective_stats.calcPrecision();
        this.recall = effective_stats.calcRecall();
    }
}
