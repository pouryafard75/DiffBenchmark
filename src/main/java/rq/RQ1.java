package rq;

/* Created by pourya on 2023-11-20 11:28 a.m. */
import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.MappingsToConsider;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static rq.Utils.mergeStats;
import static benchmark.metrics.computers.BenchmarkMetricsComputer.exportedFolderPathByCaseInfo;
import static benchmark.metrics.computers.BenchmarkMetricsComputer.getPaths;
import static benchmark.metrics.mm.writeToFile;

/* Created by pourya on 2023-09-19 6:18 p.m. */

/***
 * How many multi-mappings are missed by each tool?
 */
public class RQ1 implements RQProvider {
    private final MappingsToConsider mappingsToConsider = MappingsToConsider.MULTI_ONLY;
    private String csvDestinationFile;

    public RQ1(String csvDestinationFile) {
        this.csvDestinationFile = csvDestinationFile;
    }

    public void rq1(Configuration configuration) throws Exception {
        BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(
               configuration);
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats(mappingsToConsider);
        new MetricsCsvWriter(configuration, stats, mappingsToConsider).writeStatsToCSV(false, csvDestinationFile);
    }
    public static void verifyMultiMappings(Configuration configuration) throws IOException {
        int mm = 0 ;
        List<String > urls = new ArrayList<>();
        for (CaseInfo info : configuration.getAllCases()) {
            String folderPath = exportedFolderPathByCaseInfo(info);
            Path dir = Paths.get(configuration.getOutputFolder() + folderPath  + "/");
            List<Path> paths = getPaths(dir, 1);
            for (Path path : paths) {
                if (path.getFileName().toString().equals("mm")) continue;
                File mmFolder = new File(path.toString(), "mm");
                File GodMM = new File(mmFolder, "GOD-MM.csv");
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                HumanReadableDiff godMMHRD = mapper.readValue(GodMM, HumanReadableDiff.class);
                if (!godMMHRD.intraFileMappings.getMappings().isEmpty()
                ||
                    !godMMHRD.intraFileMappings.getMatchedElements().isEmpty())
                {
                    mm++;
                    urls.add(info.makeURL());
                    break;
                }
            }
        }
        writeToFile(urls, "rq1-mm.txt");
        System.out.println(mm);
    }

    @Override
    public void run(Configuration configuration) {
        try {
            rq1(configuration);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

