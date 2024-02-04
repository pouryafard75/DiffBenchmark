package dat;

import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.Stats;
import benchmark.oracle.models.NecessaryMappings;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/* Created by pourya on 2024-01-22*/
public class Intel {
    private final static Logger logger = LoggerFactory.getLogger(Intel.class);
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
    public static void writeIntelListToCsv(List<Intel> intelList, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Writing header
            String[] header = {
                    "repo", "commit", "srcFile",
                    "Matcher", "Name",
                    "EdSize", "EdSizeNonJavaDoc",
                    "TRV_mappings, TRV_programElements",
                    "TP_mappings, FP_mappings, FN_mappings",
                    "TP_programElements, FP_programElements, FN_programElements",
                    "Precision, Recall, F1"
            };
            writer.writeNext(header);

            // Writing data
            for (Intel intel : intelList) {
                String[] data = {
                        intel.repo, intel.commit,  intel.srcPath,
                        intel.matcher, intel.conf,
                        String.valueOf(intel.edSize),  String.valueOf(intel.edSizeNonJavaDoc),
                        String.valueOf(intel.TRV_mappings), String.valueOf(intel.TRV_programElements),
                        String.valueOf(intel.TP_mappings), String.valueOf(intel.FP_mappings), String.valueOf(intel.FN_mappings),
                        String.valueOf(intel.TP_programElements), String.valueOf(intel.FP_programElements), String.valueOf(intel.FN_programElements),
                        String.valueOf(intel.precision), String.valueOf(intel.recall), String.valueOf(intel.f1)
                };
                writer.writeNext(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
