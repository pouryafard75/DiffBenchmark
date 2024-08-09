package rq;

/* Created by pourya on 2023-11-20 11:28 a.m. */

import benchmark.metrics.computers.refactoring.RefactoringWiseBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.refactoringminer.api.RefactoringType;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

/* Created by pourya on 2023-09-19 6:18 p.m. */

/***
 * How do refactorings affect the accuracy of each tool?
 */
public class RQ4 implements RQ{
    private int minFreq = 10;
    public RQ4(int minFreq) {
        this.minFreq = minFreq;
    }
    public RQ4(){}
    private static void rq4(Configuration[] configs, int minFreq) throws Exception {
        // Create a map to store the data
        Map<RefactoringType, Integer> dist = readCsvFromFile("merged-Distribution.csv");
        Map<RefactoringType, Integer> workingDist = dist.entrySet()
                .stream()
                .filter(e -> e.getValue() >= minFreq)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (oldValue, newValue) -> oldValue,
                        LinkedHashMap::new));
        Collection<BaseDiffComparisonResult> result = new ArrayList<>();
        StringBuilder name = new StringBuilder();
        for (Configuration config : configs) {
            for (CaseInfo info : config.getAllCases()) {
                System.out.println("Running " + info.makeURL());
                result.addAll(new RefactoringWiseBenchmarkComputer(config, workingDist.keySet()).compute(info));
            }
            name.append(config.getName()).append("-");
        }
        MetricsCsvWriter.exportToCSV(result, "rq4-uniqueTypeAndMappings-" + name + ".csv", true);
    }

    private static Map<RefactoringType, Integer> readCsvFromFile(String name) {
        Map<RefactoringType, Integer> resultMap = new LinkedHashMap<>();
        InputStream inputStream = ClassLoader.getSystemResourceAsStream(name);

        try (Reader reader = new InputStreamReader(inputStream);
             CSVReader csvReader = new CSVReader(reader)) {
            // Read all records at once
            List<String[]> records = csvReader.readAll();

            // Assuming the header is the first record
            String[] header = records.get(0);
            int refactoringTypeIndex = -1;
            int countIndex = -1;

            // Find the indices of "Refactoring Type" and "Count" in the header
            for (int i = 0; i < header.length; i++) {
                if ("Refactoring Type".equals(header[i])) {
                    refactoringTypeIndex = i;
                } else if ("Count".equals(header[i])) {
                    countIndex = i;
                }
            }

            if (refactoringTypeIndex == -1 || countIndex == -1) {
                System.out.println("Header not found");
                return null;
            }


            // Skip the header and process the remaining records
            for (int i = 1; i < records.size(); i++) {
                String[] record = records.get(i);
                String refactoringTypeString = record[refactoringTypeIndex];
                int count = Integer.parseInt(record[countIndex]);
                RefactoringType refactoringType = RefactoringType.valueOf(refactoringTypeString);
                resultMap.put(refactoringType, count);
            }

        } catch (IOException | CsvException e) {
            throw new RuntimeException("");
        }
        return resultMap;
    }
    @Override
    public void run(Configuration[] confs) throws Exception {
        rq4(confs, minFreq);
    }
}

