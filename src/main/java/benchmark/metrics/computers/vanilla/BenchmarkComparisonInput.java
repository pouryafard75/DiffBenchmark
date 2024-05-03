package benchmark.metrics.computers.vanilla;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.models.HumanReadableDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.EnumMap;
import java.util.Map;

import static benchmark.utils.PathResolver.exportedFolderPathByCaseInfo;

public class BenchmarkComparisonInput {
    private Map<ASTDiffTool, HumanReadableDiff> rawHRDMap;
    private HumanReadableDiff originalGODHRD;
    private HumanReadableDiff originalTRVHRD;

    public Map<ASTDiffTool, HumanReadableDiff> getRawHRDMap() {
        return rawHRDMap;
    }

    public HumanReadableDiff getOriginalGODHRD() {
        return originalGODHRD;
    }

    public HumanReadableDiff getOriginalTRVHRD() {
        return originalTRVHRD;
    }

    BenchmarkComparisonInput(Map<ASTDiffTool, HumanReadableDiff> rawHRDMap, HumanReadableDiff originalGODHRD, HumanReadableDiff originalTRVHRD) {
        this.rawHRDMap = rawHRDMap;

        this.originalGODHRD = originalGODHRD;
        this.originalTRVHRD = originalTRVHRD;
        removeUnnecessaryHRDs();
    }

    public static BenchmarkComparisonInput read(Configuration configuration, CaseInfo info, String fileName) throws IOException {
        String folderPath = exportedFolderPathByCaseInfo(info);
        Path dir = Paths.get(configuration.getOutputFolder() + folderPath + "/");
        return readDir(dir.resolve(fileName), configuration);
    }
    public static BenchmarkComparisonInput readDir(Path finalPath, Configuration configuration) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);

        HumanReadableDiff god = null, trv = null;
        Map<ASTDiffTool, HumanReadableDiff> hrds = new EnumMap<>(ASTDiffTool.class);


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
                        ASTDiffTool astDiffTool = ASTDiffTool.valueOf(toolName);
                        if (java.util.Arrays.stream(configuration.getActiveTools()).toList().contains(astDiffTool))
                            hrds.put(astDiffTool, hrd);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (god == null || trv == null) throw new RuntimeException("GOD or TRV cannot be null");
        return new BenchmarkComparisonInput(hrds, god, trv);
    }

    private void removeUnnecessaryHRDs() {
        if (rawHRDMap == null) throw new RuntimeException("rawHRDMap cannot be null");
        rawHRDMap.remove(ASTDiffTool.GOD);
        rawHRDMap.remove(ASTDiffTool.TRV);
        if (rawHRDMap.isEmpty()) throw new RuntimeException("rawHRDMap cannot be empty");
    }

    public void add(ASTDiffTool astDiffTool, HumanReadableDiff result) {
        rawHRDMap.put(astDiffTool, result);
    }
    public void filterForDat(){
        //Only keep GOD, TRV, RM, and DAT , and remove the rest
        rawHRDMap.keySet().
                removeIf(
                        x -> !x.equals(ASTDiffTool.GOD)
                        && !x.equals(ASTDiffTool.TRV)
                        && !x.equals(ASTDiffTool.RMD)
                        && !x.equals(ASTDiffTool.DAT));

    }
}
