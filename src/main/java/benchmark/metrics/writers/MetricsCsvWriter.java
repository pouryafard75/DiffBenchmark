package benchmark.metrics.writers;

import benchmark.metrics.computers.StatsComputer;
import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.*;
import benchmark.utils.Configuration.Configuration;

import java.io.FileWriter;
import java.util.Collection;


/* Created by pourya on 2023-11-23 8:47 p.m. */
public class MetricsCsvWriter {
    public static void exportToCSV(Collection<? extends CsvWritable> compResults, String csvFilePath, boolean withHeader) {
        if (compResults.isEmpty()) {
            System.out.println("Collection is empty. Nothing to export.");
            return;
        }
        try (FileWriter writer = new FileWriter(csvFilePath))
        {
            if (withHeader) compResults.iterator().next().writeHeader(writer);
            for (CsvWritable compResult : compResults) {
                compResult.writeData(writer);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }
    public static void exportToCsv(Collection<? extends CsvWritable> compResults, StatsComputer computer){
        //todo
    }


}
