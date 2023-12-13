package benchmark.metrics.writers;

import benchmark.metrics.models.CsvWritable;

import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;


/* Created by pourya on 2023-11-23 8:47 p.m. */
public class MetricsCsvWriter {
    public static void exportToCSV(Collection<? extends CsvWritable> compResults, String csvFilePath, boolean withHeader) {
        if (compResults.isEmpty()) {
            System.out.println("Collection is empty. Nothing to export.");
            return;
        }
        try (FileWriter writer = new FileWriter(addDateAndTime(csvFilePath)))
        {
            if (withHeader) compResults.iterator().next().writeHeader(writer);
            for (CsvWritable compResult : compResults) {
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
