import benchmark.utils.ChurnCalculator;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
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
        Pair<Integer, Integer> churn = ChurnCalculator.calculateAddDeleteChurn(projectASTDiff);
        assertEquals(expectedAdded, churn.getLeft());
        assertEquals(expectedDeleted,churn.getRight());
    }
}