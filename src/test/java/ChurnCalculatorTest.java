import benchmark.metrics.computers.churn.ChurnCalculator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ChurnCalculatorTest {
    @ParameterizedTest
    @CsvSource({
            // CommitUrl, expectedAdded, expectedDeleted
            "https://github.com/JetBrains/intellij-community/commit/97811cf971f7ccf6a5fc5e90a491db2f58d49da1, 143, 110",
            "https://github.com/liferay/liferay-plugins/commit/7c7ecf4cffda166938efd0ae34830e2979c25c73, 42, 22",
            "https://github.com/apache/hive/commit/f664789737d516ac664462732664121acc111a1e, 25, 9",
    })
    public void calculateAddDeleteChurnTest(String url, int expectedAdded, int expectedDeleted) {
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(URLHelper.getRepo(url), URLHelper.getCommit(url), 1000);
        Pair<Integer, Integer> churn = ChurnCalculator.calculateAddDeleteChurn(projectASTDiff, true,true);
        assertEquals(expectedAdded, churn.getLeft());
        assertEquals(expectedDeleted,churn.getRight());
    }
    @ParameterizedTest
    @CsvSource({
            // CommitUrl, relativeAdded, relativeDeleted
            "https://github.com/JetBrains/intellij-community/commit/e1f0dbc2f09541fc64ce88ee22d8f8f4648004fe, 0.039906103, 0.016826924",
            "https://github.com/JetBrains/intellij-community/commit/a97341973c3b683d62d1422e5404ed5c7ccf45f8,0.077809796,0.012345679",
            "https://github.com/openhab/openhab1-addons/commit/cf1efb6d27a4037cdbe5a780afa6053859a60d4a,0.39558232,0.11470588"


    })
    public void calculateRelativeAddDeleteChurnTest(String url, float relativeAdded, float relativeDeleted) {
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(URLHelper.getRepo(url), URLHelper.getCommit(url), 1000);
        Pair<Float, Float> churn = ChurnCalculator.calculateRelativeAddDeleteChurn(projectASTDiff,true,true);
        assertEquals(relativeAdded, churn.getLeft());
        assertEquals(relativeDeleted,churn.getRight());

    }
}