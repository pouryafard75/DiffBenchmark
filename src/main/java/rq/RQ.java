package rq;

import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.IASTDiffTool;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public interface RQ {
    void run(IExperiment[] confs) throws Exception;

    static <V> void writeToFile(Map<IASTDiffTool, V> astDiffToolIntegerMap, String outputFilePath) {
        File file = new File(outputFilePath);
        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            // iterate map entries
            for (Map.Entry<IASTDiffTool, V> entry :
                    astDiffToolIntegerMap.entrySet()) {
                // put key and value separated by a colon
                bf.write(entry.getKey().getToolName() + ":"
                        + entry.getValue());
                // new line
                bf.newLine();
            }
            bf.flush();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                bf.close();
            }
            catch (Exception e) {
            }
        }
    }
}
