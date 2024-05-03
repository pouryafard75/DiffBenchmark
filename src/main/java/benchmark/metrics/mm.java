package benchmark.metrics;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static benchmark.generators.tools.ASTDiffTool.*;
import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-09-19 6:18 p.m. */
public class mm {
    public static void main(String[] args) throws Exception {
        run();
    }
    public static void run() throws Exception {
        Configuration configuration = ConfigurationFactory.refOracle();
        ASTDiffTool[] experiment_tools = new ASTDiffTool[]{GTG,GTS,RMD,MTD,IJM,GT2,GOD};
        List<String> urls = new ArrayList<>();
        int mm = 0;
        for (CaseInfo info : configuration.getAllCases()) {
            System.out.println(mm);
            System.out.println("Working on " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet()) {
                ASTDiff perfect = GOD.diff(projectASTDiff, rm_astDiff, info, configuration);
                if (!perfect.getAllMappings().dstToSrcMultis().isEmpty() ||
                        !perfect.getAllMappings().srcToDstMultis().isEmpty())
                {
                    urls.add(info.makeURL());
                    mm++;
                    break;
                }
            }
        }
        writeToFile(urls, "mm-commits.csv");
        System.out.println(mm);
    }
    public static void writeToFile(List<String> input, String filePath){
        // Write the strings to the file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String str : input) {
                writer.write(str);
                writer.newLine(); // Add a newline after each string
            }
            System.out.println("Strings have been written to the file successfully.");
        } catch (IOException e) {
            System.err.println("Error writing to the file: " + e.getMessage());
        }
    }
}

