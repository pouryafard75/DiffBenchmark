package benchmark.data.diffcase;

import benchmark.data.dataset.IBenchmarkDataset;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.nio.file.Path;
import java.util.Set;

public interface IBenchmarkCase {
    /**
     * Get the repository/project name
     */
    String getRepo();
    /**
     * Get the commit id
     */
    String getCommit();
    /**
     * Get the unique identifier of the case
     */
    String getID();
    /**
     * Get the dataset of the case
     */
    IBenchmarkDataset getDataset();
    /**
     * Get the RefactoringMiner ProjectASTDiff for the case
     */
    ProjectASTDiff getProjectASTDiff();
    /**
     * Get the relative path from the dataset directory
     */
    default Path getRelativePathFromDatasetDir() {
        return Path.of(getRepo(), getCommit()); //repo + "/" (separator) + commit;
    }
//
//    public static void main(String[] args) {
//        IBenchmarkCase case1 = new IBenchmarkCase() {
//            @Override
//            public String getRepo() { return "Apache/commons-lang";}
//            @Override
//            public String getCommit() { return ""; }
//        };
//        IBenchmarkCase case2 = new IBenchmarkCase() {
//            @Override
//            public String getRepo() { return "Apache/commons-lang";}
//            @Override
//            public String getCommit() { return ""; }
//        };
//        IBenchmarkDataset dataset = new IBenchmarkDataset() {
//            @Override
//            public String getDatasetName() { return "CaseStudyDataset"; }
//            @Override
//            public Path getPerfectDirPath() { return Path.of("path/to/perfect/diffs"); }
//            @Override
//            public Set<? extends IBenchmarkCase> getCases() { return Set.of(case1, case2); }
//        };
//    }
}
