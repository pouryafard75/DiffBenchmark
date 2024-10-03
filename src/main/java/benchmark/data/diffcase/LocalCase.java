package benchmark.data.diffcase;

import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.nio.file.Path;

/* Created by pourya on 2024-09-28*/
public abstract class LocalCase extends AbstractIBenchmarkCase {
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
        projectASTDiff = new GitHistoryRefactoringMinerImpl().
                diffAtDirectories(srcPath, dstPath);
        System.out.println("Finished computing projectASTDiff for " + srcPath + " " + dstPath);
    }
}
