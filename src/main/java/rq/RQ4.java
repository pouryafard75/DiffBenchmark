package rq;

/* Created by pourya on 2023-11-20 11:28 a.m. */
import benchmark.metrics.computers.BenchmarkMetricsComputer;
import benchmark.metrics.computers.filters.MappingsLocationFilter;
import benchmark.metrics.computers.filters.MappingsTypeFilter;
import benchmark.metrics.models.DiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.oracle.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.util.*;
import java.util.Map.Entry;

import static rq.Utils.mergeStats;
import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-09-19 6:18 p.m. */

/***
 * How do refactorings affect the accuracy of each tool?
 */
public class RQ4 implements RQProvider {

    private static final MappingsLocationFilter mappingsLocationFilter = MappingsLocationFilter.NO_FILTER;
    private static final MappingsTypeFilter mappingsTypeFilter = MappingsTypeFilter.NO_FILTER;
    private int maxRefCount = 101;
    private int minFreq = 3;
    private String csvDestinationFile = "xyz.csv"; //TODO

    public void setCsvDestinationFile(String csvDestinationFile) {
        this.csvDestinationFile = csvDestinationFile;
    }

    public void setMaxRefCount(int maxRefCount) {
        this.maxRefCount = maxRefCount;
    }

    public void setMinFreq(int minFreq) {
        this.minFreq = minFreq;
    }

    @Override
    public void run(Configuration configuration) {
        try {
            rq4(configuration, maxRefCount, minFreq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void rq4(Configuration configuration, int maxRefCount, int minFreq) throws Exception {
        Map<Integer, Integer> countDist = Utils.refactoringCountDist(ConfigurationFactory.refOracleTwoPointOne());
        Map<Integer,DiffComparisonResult> refCountStats = new HashMap<>();
        populateRefCountStats(configuration, maxRefCount, minFreq, countDist, refCountStats);
        List<DiffComparisonResult> stats = new ArrayList<>(refCountStats.values());
        stats.sort(Comparator.comparingInt(o -> Integer.parseInt(o.getSrcFileName())));
        new MetricsCsvWriter(configuration, stats, mappingsLocationFilter, mappingsTypeFilter).writeStatsToCSV(true, this.csvDestinationFile);
    }

    private static void populateRefCountStats(Configuration configuration, int maxRefCount, int minFreq, Map<Integer, Integer> countDist, Map<Integer, DiffComparisonResult> refCountStats) throws Exception {
        for (CaseInfo caseInfo : configuration.getAllCases()) {
            ProjectASTDiff projectASTDiff = runWhatever(caseInfo.getRepo(), caseInfo.getCommit());
            int numOfRef = projectASTDiff.getRefactorings().size();
            if (numOfRef > maxRefCount || countDist.get(numOfRef) < minFreq) continue;
            System.out.println(caseInfo.makeURL() + " has " + numOfRef + " refactorings");
            DiffComparisonResult existing =
                    refCountStats.getOrDefault(numOfRef,
                            new DiffComparisonResult(
                                    new CaseInfo("RefCount",String.valueOf(numOfRef)), String.valueOf(numOfRef)));
            ArrayList<DiffComparisonResult> oneCaseStats = new ArrayList<>();
            new BenchmarkMetricsComputer(configuration).oneCaseStats(caseInfo, oneCaseStats, mappingsLocationFilter, mappingsTypeFilter);
            if (existing.getDiffStatsList().isEmpty()){
                for (Entry<String, DiffStats> entry : oneCaseStats.get(0).getDiffStatsList().entrySet()) {
                    existing.getDiffStatsList().put(entry.getKey(), new DiffStats());
                }
            }
            if (existing.getIgnore() == null){
                existing.setIgnore(new HumanReadableDiff());
            }
            Utils.mergeStats(existing, oneCaseStats);
            refCountStats.put(numOfRef, existing);
        }
    }
}

