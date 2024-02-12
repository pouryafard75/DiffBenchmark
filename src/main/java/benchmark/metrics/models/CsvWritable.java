package benchmark.metrics.models;

import java.io.FileWriter;

public interface CsvWritable extends Comparable<CsvWritable> {
    void writeData(FileWriter writer) throws Exception;
    void writeHeader(FileWriter writer) throws Exception;
}
