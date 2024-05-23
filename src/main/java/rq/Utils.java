package rq;

import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.Stats;
import benchmark.models.HumanReadableDiff;
import benchmark.models.NecessaryMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.opencsv.CSVWriter;
import org.refactoringminer.api.Refactoring;
import org.refactoringminer.api.RefactoringType;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-11-21 11:55 p.m. */
public class Utils {
    public static void mergeStats(BaseDiffComparisonResult existing, Collection<? extends BaseDiffComparisonResult> oneCaseStats) {
        if (existing.getIgnore() == null){
            existing.setIgnore(new HumanReadableDiff());
        }
        for (String toolName : oneCaseStats.iterator().next().getDiffStatsList().keySet()) {
            existing.putStats(toolName, new DiffStats());
        }
        for (BaseDiffComparisonResult oneCaseStat : oneCaseStats) {
            if (!oneCaseStat.getIgnore().getInterFileMappings().isEmpty()){
                throw new RuntimeException("Inter file mappings are not empty");
            }
            NecessaryMappings trivial = oneCaseStat.getIgnore().getIntraFileMappings();
            existing.addToIgnore(trivial);
        }

        for (Map.Entry<String, DiffStats> entry : existing.getDiffStatsList().entrySet()) {
            String key = entry.getKey();
            DiffStats value = entry.getValue();
            for (BaseDiffComparisonResult oneCaseStat : oneCaseStats) {
                DiffStats diffStats = oneCaseStat.getDiffStatsList().get(key);
                value = mergeStats(value, diffStats);
            }
            existing.putStats(key, value);
        }
    }

    public static DiffStats mergeStats(DiffStats value, DiffStats diffStats) {
        return new DiffStats(
                mergeStats(value.getProgramElementStats(), diffStats.getProgramElementStats()),
                mergeStats(value.getAbstractMappingStats(), diffStats.getAbstractMappingStats())
        );
    }

    public static Stats mergeStats(Stats stat1, Stats stat2) {
        return new Stats(
                stat1.getTP() + stat2.getTP(),
                stat1.getFP() + stat2.getFP(),
                stat1.getFN() + stat2.getFN()
        );
    }

    static Map<RefactoringType, Integer> refactoringTypeDist(Configuration configuration) throws IOException {
        return refDistribution(configuration, null, Refactoring::getRefactoringType);
    }
    static Map<Integer, Integer> refactoringCountDist(Configuration configuration) throws IOException {
        return refDistribution(configuration, List::size, null);
    }

    private static <T> Map<T, Integer> refDistribution(Configuration configuration, Function<List<Refactoring>, T> perCollectionFunction, Function<Refactoring, T> perEachFunction) throws IOException {
        Map<T, Integer> countMap = new HashMap<>();
        for (CaseInfo info : configuration.getAllCases()) {
            System.out.println("Working on " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            List<Refactoring> refactorings = projectASTDiff.getRefactorings();
            if (perCollectionFunction != null && perEachFunction != null) throw new RuntimeException("One must be null");
            if (perCollectionFunction != null) {
                T key = perCollectionFunction.apply(refactorings);
                int prev = countMap.getOrDefault(key,0);
                countMap.put(key,prev + 1);
            }
            else if (perEachFunction != null) {
                for (Refactoring refactoring : refactorings) {
                    T key = perEachFunction.apply(refactoring);
                    int prev = countMap.getOrDefault(key,0);
                    countMap.put(key,prev + 1);
                }
            }
            else {
                throw new RuntimeException("Both functions are null");
            }
        }
        return sortBasedOnValues(countMap);
    }

    private static <T> Map<T, Integer> sortBasedOnValues(Map<T, Integer> countMap) {
        List<Map.Entry<T, Integer>> entryList = new ArrayList<>(countMap.entrySet());

        // Use Collections.sort with a custom comparator
        entryList.sort(Collections.reverseOrder(Map.Entry.comparingByValue()));
        Map<T, Integer> result = new LinkedHashMap<>();
        for (Map.Entry<T, Integer> entry : entryList) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

    public static <T> void writeRecordsToCSV(Map<T, Integer> refactoringTypeIntegerMap, String filePath, String colName) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        CSVWriter csvWriter = new CSVWriter(fileWriter);
        String[] header = {colName, "Count"};
        csvWriter.writeNext(header);
        for (Map.Entry<T, Integer> entry : refactoringTypeIntegerMap.entrySet()) {
            String[] data = {String.valueOf(entry.getKey()), String.valueOf(entry.getValue())};
            csvWriter.writeNext(data);
        }
        csvWriter.close();
    }

}
