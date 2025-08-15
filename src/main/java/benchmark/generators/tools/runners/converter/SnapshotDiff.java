package benchmark.generators.tools.runners.converter;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.models.selector.DiffSelector;

import static org.refactoringminer.astDiff.utils.ExportUtils.getFileNameFromSrcDiff;
import static org.refactoringminer.astDiff.utils.ExportUtils.repoToFolder;

public class SnapshotDiff extends PerfectDiff{

    public SnapshotDiff(IBenchmarkCase benchmarkCase, DiffSelector querySelector) {
        super(benchmarkCase, querySelector);
    }

    @Override
    protected String getFileNameBasedOnAST() {
        String s = benchmarkCase.getDataset().getPerfectDirPath() + "/" + repoToFolder(this.benchmarkCase.getRepo()) + "/" + benchmarkCase.getCommit() + "/" + getFileNameFromSrcDiff(input.getSrcPath());
        return s.replace("/resources/astDiff/", "/resources/astDiff/PREV-SNAPSHOT/");
    }
}



