package benchmark.utils;

import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.IASTDiffTool;
import com.github.gumtreediff.matchers.Mapping;
import org.apache.commons.io.FileUtils;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

/* Created by pourya on 2025-01-09*/
public class ExportHelpers {
    public static void exportTool(ASTDiffToolEnum tool, Map<IASTDiffTool, Set<ASTDiff>> diffs, String dstFolder) throws IOException {
        ASTDiff next = diffs.get(tool).iterator().next();
        FileUtils.write(new File(dstFolder + "src_" + tool.name() + ".txt"), next.src.getRoot().toTreeString());
        FileUtils.write(new File(dstFolder + "dst_" + tool.name() + ".txt"), next.dst.getRoot().toTreeString());
        Set<Mapping> mappings = next.getAllMappings().getMappings();
        StringBuilder ms = new StringBuilder();
        for (Mapping mapping : mappings) {
            ms.append(mapping.first).append(" -> ").append(mapping.second).append("\n");
        }
        FileUtils.write(new File(dstFolder + "mappings_" + tool.name() + ".txt"), ms);
    }
}
