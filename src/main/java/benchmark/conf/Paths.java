package benchmark.conf;

/* Created by pourya on 2024-09-28*/
public class Paths {
    // Update value to the RefactoringMiner cloned repository path in your hard drive
    private static final String REFACTORING_MINER_PATH = "/Users/pourya/IdeaProjects/RM-ASTDiff/";

    public static final String FINALIZED_REFACTORING_MINER_PATH = System.getProperty("REFACTORINGMINER_PATH", REFACTORING_MINER_PATH);
    public static final String ORACLE_DIR = FINALIZED_REFACTORING_MINER_PATH + "/src/test/resources/oracle/commits/";

}
