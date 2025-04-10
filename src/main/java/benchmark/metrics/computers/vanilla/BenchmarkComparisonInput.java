package benchmark.metrics.computers.vanilla;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.IExperiment;
import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.models.hrd.HumanReadableDiff;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static benchmark.utils.PathResolver.exportedFolderPathByCaseInfo;

public class
BenchmarkComparisonInput {
    private Map<ASTDiffToolEnum, HumanReadableDiff> rawHRDMap;
    private HumanReadableDiff originalGODHRD;
    private HumanReadableDiff originalTRVHRD;

    public Map<ASTDiffToolEnum, HumanReadableDiff> getRawHRDMap() {
        return rawHRDMap;
    }

    public HumanReadableDiff getOriginalGODHRD() {
        return originalGODHRD;
    }

    public HumanReadableDiff getOriginalTRVHRD() {
        return originalTRVHRD;
    }

    BenchmarkComparisonInput(Map<ASTDiffToolEnum, HumanReadableDiff> rawHRDMap, HumanReadableDiff originalGODHRD, HumanReadableDiff originalTRVHRD) {
        this.rawHRDMap = rawHRDMap;
        this.originalGODHRD = originalGODHRD;
        this.originalTRVHRD = originalTRVHRD;
//        removeUnnecessaryHRDs();
    }

    public static BenchmarkComparisonInput read(IExperiment experiment, IBenchmarkCase info, String fileName, Set<IASTDiffTool> diffIgnoreGuards) throws IOException {
        String folderPath = exportedFolderPathByCaseInfo(info);
        Path dir = Paths.get(experiment.getOutputFolder() + folderPath + "/");
        HashSet<IASTDiffTool> withGuard = new HashSet<>(experiment.getTools());
        withGuard.addAll(diffIgnoreGuards);
        return readDir(dir.resolve(fileName), withGuard);
    }
    public static BenchmarkComparisonInput readDir(Path finalPath, Set<IASTDiffTool> tools) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        HumanReadableDiff god = null, trv = null;
        Map<ASTDiffToolEnum, HumanReadableDiff> hrds = new EnumMap<>(ASTDiffToolEnum.class);

        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(finalPath)) {
            for (Path filePath : directoryStream) {
                String toolName = filePath.getFileName().toString().replace(".json", "");
                HumanReadableDiff hrd = mapper.readValue(new File(filePath.toString()), HumanReadableDiff.class);
                switch (toolName) {
                    case "GOD":
                        god = hrd;
                        break;
                    case "TRV":
                        trv = hrd;
                        break;
                    default:
                        ASTDiffToolEnum astDiffTool = ASTDiffToolEnum.valueOf(toolName);
                        if (tools.contains(astDiffTool))
                            hrds.put(astDiffTool, hrd);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (god == null || trv == null)
            throw new RuntimeException("GOD or TRV cannot be null");
        return new BenchmarkComparisonInput(hrds, god, trv);
    }

    private void removeUnnecessaryHRDs() {
        if (rawHRDMap == null) throw new RuntimeException("rawHRDMap cannot be null");
        rawHRDMap.remove(ASTDiffToolEnum.GOD);
        rawHRDMap.remove(ASTDiffToolEnum.TRV);
        if (rawHRDMap.isEmpty()) throw new RuntimeException("rawHRDMap cannot be empty");
    }

    public void add(ASTDiffToolEnum ASTDiffToolEnum, HumanReadableDiff result) {
        rawHRDMap.put(ASTDiffToolEnum, result);
    }
    public void filterForDat(){
        //Only keep GOD, TRV, RM, and DAT , and remove the rest
        rawHRDMap.keySet().
                removeIf(
                        x -> !x.equals(ASTDiffToolEnum.GOD)
                        && !x.equals(ASTDiffToolEnum.TRV)
                        && !x.equals(ASTDiffToolEnum.RMD)
                        && !x.equals(ASTDiffToolEnum.DAT));

    }
}
