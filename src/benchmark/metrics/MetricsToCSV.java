package benchmark.metrics;

import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.utils.CaseInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static benchmark.metrics.computers.BenchmarkMetricsComputer.writeStatsToCSV;
import static benchmark.utils.Configuration.casesPath;

/* Created by pourya on 2023-04-16 4:16 a.m. */
public class MetricsToCSV {
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        List<CaseInfo> infos = mapper.readValue(new File(casesPath), new TypeReference<List<CaseInfo>>(){});


        BenchmarkMetricsComputer benchmarkMetricsComputer = new BenchmarkMetricsComputer(infos);
        List<DiffComparisonResult> stats = benchmarkMetricsComputer.generateBenchmarkStats();
        String[] activeTools = benchmarkMetricsComputer.getActiveTools();
        writeStatsToCSV(stats,activeTools);
    }
}
