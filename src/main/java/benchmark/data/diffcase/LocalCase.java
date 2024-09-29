package benchmark.data.diffcase;

import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.nio.file.Path;

/* Created by pourya on 2024-09-28*/
public abstract class LocalCase extends AbstractBenchmarkCase {
    final Path srcPath;
    final Path dstPath;

    public Path getSrcPath() {
        return srcPath;
    }

    public Path getDstPath() {
        return dstPath;
    }

    public LocalCase(Path srcPath, Path dstPath) {
        this.srcPath = srcPath;
        this.dstPath = dstPath;
    }

    @Override
    public ProjectASTDiff getProjectASTDiff() {
        return new GitHistoryRefactoringMinerImpl().
                diffAtDirectories(srcPath, dstPath);
    }
}
