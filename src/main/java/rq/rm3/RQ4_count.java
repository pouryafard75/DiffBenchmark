package rq.rm3;

/* Created by pourya on 2023-11-20 11:28 a.m. */

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.diffcase.RefCountCaseI;
import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringGeneration;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.CommitRefactoringCountComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.writers.MetricsCsvWriter;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.*;
import java.util.Map.Entry;

/* Created by pourya on 2023-09-19 6:18 p.m. */

/***
 * How do refactorings affect the accuracy of each tool?
 */
public class RQ4_count{
    private static final FilterDuringGeneration FILTER_DURING_GENERATION = FilterDuringGeneration.NO_FILTER;
    private static final FilterDuringMetricsCalculation FILTER_DURING_METRICS_CALCULATION = FilterDuringMetricsCalculation.NO_FILTER;
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

    public void run(IExperiment experiment) {
        try {
            rq4(experiment, maxRefCount, minFreq);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public void rq4(IExperiment experiment, int maxRefCount, int minFreq) throws Exception {
        Map<Integer, Integer> countDist = Utils.refactoringCountDist(ExperimentsEnum.REF_EXP_2_1);
        Map<Integer, CommitRefactoringCountComparisonResult> refCountStats = new HashMap<>();
        populateRefCountStats(experiment, maxRefCount, minFreq, countDist, refCountStats);
        List<CommitRefactoringCountComparisonResult> stats = new ArrayList<>(refCountStats.values());
        stats.sort(Comparator.comparingInt(CommitRefactoringCountComparisonResult::getNumOfRefactorings));
        //TODO: MUST DEFINE ANOTHER COMPUTER FOR THIS TASK
        MetricsCsvWriter.exportToCSV(stats, "rq4.csv",true, "out/");
//        new MetricsCsvWriter(configuration, stats, mappingsLocationFilter, mappingsTypeFilter).writeStatsToCSV(true, this.csvDestinationFile);

    }

    private static void populateRefCountStats(IExperiment experiment, int maxRefCount, int minFreq, Map<Integer, Integer> countDist, Map<Integer, CommitRefactoringCountComparisonResult> refCountStats) throws Exception {
        for (IBenchmarkCase caseInfo : experiment.getDataset().getCases()) {
            ProjectASTDiff projectASTDiff = caseInfo.getProjectASTDiff();
            int numOfRef = projectASTDiff.getRefactorings().size();
            if (numOfRef > maxRefCount || countDist.get(numOfRef) < minFreq) continue;
            System.out.println(caseInfo.getID() + " has " + numOfRef + " refactorings");
            CommitRefactoringCountComparisonResult existing =
                    refCountStats.getOrDefault(numOfRef,
                            new CommitRefactoringCountComparisonResult(
                                    new RefCountCaseI(numOfRef), numOfRef));
            Collection<? extends BaseDiffComparisonResult> oneCaseStats = new VanillaBenchmarkComputer(experiment, FILTER_DURING_GENERATION.getFilter(), FILTER_DURING_METRICS_CALCULATION).compute(caseInfo);
            if (existing.getDiffStatsList().isEmpty()){
                for (Entry<String, DiffStats> entry : oneCaseStats.iterator().next().getDiffStatsList().entrySet()) {
                    existing.getDiffStatsList().put(entry.getKey(), new DiffStats());
                }
            }
            Utils.mergeStats(existing, oneCaseStats);
            refCountStats.put(numOfRef, existing);
        }
    }
}

