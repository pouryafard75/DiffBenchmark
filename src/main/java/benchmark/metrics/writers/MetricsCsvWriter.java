package benchmark.metrics.writers;

import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.CsvWritable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;


/* Created by pourya on 2023-11-23 8:47 p.m. */
public class MetricsCsvWriter {
    public static void exportToCSV(Collection<? extends CsvWritable> compResults, String csvFilePath, boolean withHeader) {
        csvFilePath = "out/" + csvFilePath;
        if (compResults.isEmpty()) {
            System.out.println("Collection is empty. Nothing to export.");
            return;
        }
        try {
            Files.createDirectories(Paths.get(csvFilePath).getParent());
        } catch (IOException e) {
            e.printStackTrace();
            return; // Exit if directory creation fails
        }
        List<CsvWritable> sortedResult = new ArrayList<>(compResults);
        Collections.sort(sortedResult);
        try (FileWriter writer = new FileWriter(addDateAndTime(csvFilePath)))
        {
            if (withHeader) sortedResult.iterator().next().writeHeader(writer);
            for (CsvWritable compResult : sortedResult) {
                compResult.writeData(writer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private static String addDateAndTime(String csvFilePath) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy_MM_dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        return csvFilePath.replace(".csv", "-" + dtf.format(now) + ".csv");
    }
}
