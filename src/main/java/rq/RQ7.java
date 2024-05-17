package rq;

/* Created by pourya on 2023-12-04 2:15 p.m. */

import benchmark.metrics.computers.vanilla.CommitPerfectRatioBenchmarkComputer;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/***
 * What's the perfection ratio for each tool
 */
public class RQ7 implements RQ  {
    @Override
    public void run(Configuration[] conf) {
        if (conf.length > 1) {
            throw new RuntimeException("RQ7 accepts only one configuration");
        }
        Configuration configuration = conf[0];
        try {
            rq7(configuration);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void rq7(Configuration configuration) throws IOException {
        Map<ASTDiffTool, Integer> astDiffToolIntegerMap = new CommitPerfectRatioBenchmarkComputer(configuration).perfectRatio();
        writeToFile(astDiffToolIntegerMap, "out/rq7-" + configuration.getName() + ".txt");
    }
    static void writeToFile(Map<ASTDiffTool, Integer> astDiffToolIntegerMap, String outputFilePath) {
        File file = new File(outputFilePath);

        BufferedWriter bf = null;
        try {
            bf = new BufferedWriter(new FileWriter(file));
            // iterate map entries
            for (Map.Entry<ASTDiffTool, Integer> entry :
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
