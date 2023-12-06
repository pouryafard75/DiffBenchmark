package benchmark.metrics;

import benchmark.metrics.computers.churn.ChurnCalculator;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;
import java.io.IOException;

import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;

/* Created by pourya on 2023-12-05 9:52 p.m. */
public class Characteristics {
    public static void main(String[] args) throws IOException {
        Configuration configuration = ConfigurationFactory.refOracle();
        float totalLeft = 0.0f;
        float totalRight = 0.0f;
        int commitCount = 0;
        for (CaseInfo info : configuration.getAllCases()) {
            System.out.println("Processing: " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI(
                    info.getRepo(), info.getCommit(), new File(ORACLE_DIR));
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
        System.out.println((averageLeft + averageRight) / 2);
    }

}

//execution output with true,ture: Average Left Churn: 0.1361895 Average Right Churn: 0.0884807