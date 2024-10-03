package benchmark.metrics.computers.vanilla;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.metrics.computers.BaseBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.generators.tools.ASTDiffToolEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static benchmark.utils.PathResolver.exportedFolderPathByCaseInfo;
import static benchmark.utils.PathResolver.getPaths;

/* Created by pourya on 2023-12-04 2:19 p.m. */
public class CommitPerfectRatioBenchmarkComputer extends BaseBenchmarkComputer {


    public CommitPerfectRatioBenchmarkComputer(IExperiment experiment) {
        super(experiment);
    }

    public Map<IASTDiffTool, Integer> perfectRatio() throws IOException {
        Map<IASTDiffTool, Integer> result = new LinkedHashMap<>();
        for (IASTDiffTool tool : getExperiment().getTools()) {
            result.put(tool, 0);
        }
        for (IBenchmarkCase info : getExperiment().getDataset().getCases()) {
            String folderPath = exportedFolderPathByCaseInfo(info);
            Path dir = Paths.get(getExperiment().getOutputFolder() + folderPath  + "/");
//            System.out.println("Generating benchmark stats for " + info.getRepo() + " " + info.getCommit());
            List<Path> paths = getPaths(dir, 1);
            for (IASTDiffTool tool : getExperiment().getTools()) {
                boolean miss = false;
                for (Path dirPath : paths) {
                    String toolPath = tool.getToolName();
                    String godFullPath = dirPath.resolve(ASTDiffToolEnum.GOD.name() + ".json").toString();
                    String toolFullPath = godFullPath.replace(ASTDiffToolEnum.GOD.name(), toolPath);
                    if (!areFileContentsEqual(Paths.get(godFullPath), Paths.get(toolFullPath))) {
                        miss = true;
                        break;
                    }
                }
                if (!miss) result.put(tool, result.getOrDefault(tool, 0) + 1);
                }
            }
        return result;
    }

    private static boolean areFileContentsEqual(Path file1, Path file2) throws IOException {
        byte[] content1 = Files.readAllBytes(file1);
        byte[] content2 = Files.readAllBytes(file2);
        return Arrays.equals(content1, content2);
    }

    @Override
    public Collection<? extends BaseDiffComparisonResult> compute() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends BaseDiffComparisonResult> compute(IBenchmarkCase info) throws IOException {
        throw new UnsupportedOperationException();
    }
}
