package benchmark;

import benchmark.metrics.computers.churn.ChurnCalculator;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.io.IOException;

import static benchmark.oracle.generators.tools.models.ASTDiffTool.GOD;
import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-12-05 9:52 p.m. */
public class Characteristics {
    public static void main(String[] args) throws IOException {
        printCharacteristics(ConfigurationFactory.refOracle());
        printCharacteristics(ConfigurationFactory.defects4j());
    }

    private static void printCharacteristics(Configuration configuration) throws IOException {
        int size = configuration.getAllCases().size();

        float avgChurn = getAvgChurn(configuration);
        int numOfRef = getNumOfCasesWithRefactoring(configuration);
        int numOfMultiMappings = getNumOfMultiMappings(configuration);

        System.out.println(configuration.getName());
        System.out.println("Average Churn: " + avgChurn);
        System.out.println("Number of cases containing refactoring: " + numOfRef + " which is " + (float) numOfRef / size * 100 + "%");
        System.out.println("Number of cases containing multi-mappings: " + numOfMultiMappings + " which is " + (float) numOfMultiMappings / size * 100 + "%");
    }

    private static int getNumOfMultiMappings(Configuration configuration) {
        int count = 0;
        for (CaseInfo info : configuration.getAllCases()) {
            System.out.println("Processing: " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            boolean hasMultiMappings = false;
            for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
                ASTDiff diff = GOD.getFactory().getASTDiff(projectASTDiff, astDiff, info, configuration);
                if (!diff.getAllMappings().srcToDstMultis().isEmpty() || !diff.getAllMappings().dstToSrcMultis().isEmpty()) {
                    hasMultiMappings = true;
                    break;
                }
            }
            if (hasMultiMappings) {
                count++;
            }
        }
        return count;
    }

    private static int getNumOfCasesWithRefactoring(Configuration configuration) {
        int count = 0;
        for (CaseInfo info : configuration.getAllCases()) {
            System.out.println("Processing: " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            if (!projectASTDiff.getRefactorings().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    private static float getAvgChurn(Configuration configuration) throws IOException {
        float totalLeft = 0.0f;
        float totalRight = 0.0f;
        int commitCount = 0;
        for (CaseInfo info : configuration.getAllCases()) {
            System.out.println("Processing: " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            Pair<Float, Float> floatFloatPair = ChurnCalculator.calculateRelativeAddDeleteChurn
                (projectASTDiff, false, false);
            float leftChurn = floatFloatPair.getLeft();
            float rightChurn = floatFloatPair.getRight();

            // Accumulate values for left and right floats
            totalLeft += leftChurn;
            totalRight += rightChurn;

            commitCount++;
        }
        float averageLeft = totalLeft / commitCount;
        float averageRight = totalRight / commitCount;


//        System.out.println("Average Left Churn: " + averageLeft);
//        System.out.println("Average Right Churn: " + averageRight);
        return (averageLeft + averageRight) / 2;
    }

}
