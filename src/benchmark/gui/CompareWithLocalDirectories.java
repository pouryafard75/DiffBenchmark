package benchmark.gui;

import benchmark.gui.web.BenchmarkWebDiff;
import benchmark.gui.web.BenchmarkWebDiffFactory;
import benchmark.utils.CaseInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import org.apache.commons.lang3.math.NumberUtils;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ProjectASTDiffer;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2023-05-02 5:15 p.m. */
public class CompareWithLocalDirectories {
    static String projectDir = "Jsoup";
    static String bugID = "75";
    public static void main(String[] args) throws IOException {

        BenchmarkWebDiff benchmarkWebDiff = null;
        try {
            benchmarkWebDiff = BenchmarkWebDiffFactory.withLocallyClonedRepo(
                    getBeforeDir(projectDir, bugID), getAfterDir(projectDir, bugID),new CaseInfo(projectDir, bugID));
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
//        benchmarkWebDiff.ijmDiff = loadCustomSet(benchmarkWebDiff, "_m.json");
        benchmarkWebDiff.run();
//        int i = 0;
//        for (Diff diff : benchmarkWebDiff.gtgDiff)
//        {
//            MappingExportModel.exportToFile(new File(i + " gtg.json"),diff.mappings);
//            i ++;
//        }
//////
//        for (ASTDiff diff : benchmarkWebDiff.rmDiff)
//        {
//            MappingExportModel.exportToFile(new File("rmd.json"),diff.getAllMappings());
//            i ++;
//        }

//        MappingExportModel.exportToFile(new File("_rmd.json"),benchmarkWebDiff.rmDiff.iterator().next().getAllMappings());
    }

    public static Set<Diff> loadCustomSet(BenchmarkWebDiff benchmarkWebDiff, String filePath) {
        Set<Diff> tempSet = new HashSet<>();
        Diff gts = benchmarkWebDiff.gtsDiff.iterator().next();
        MappingStore modified_mappings = new MappingStore(gts.src.getRoot(),gts.dst.getRoot());
        ObjectMapper mapper = new ObjectMapper();
        List<MappingExportModel> mappingExportModelList = null;
        try {
            mappingExportModelList = mapper.readValue(new File(filePath), new TypeReference<List<MappingExportModel>>(){});
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        for (MappingExportModel mappingExportModel : mappingExportModelList) {
            Tree t1 = TreeUtilFunctions.getTreeBetweenPositionsSecure(gts.src.getRoot(), mappingExportModel.getFirstPos(), mappingExportModel.getFirstEndPos(), mappingExportModel.getFirstType(),mappingExportModel.getFirstParentType(),mappingExportModel.getFirstLabel());
            Tree t2 = TreeUtilFunctions.getTreeBetweenPositionsSecure(gts.dst.getRoot(),mappingExportModel.getSecondPos(),mappingExportModel.getSecondEndPos(),mappingExportModel.getSecondType(),mappingExportModel.getSecondParentType(),mappingExportModel.getSecondLabel());
            if (t1 == null || t2 == null)
                throw new RuntimeException("null tree");

            modified_mappings.addMapping(t1,t2);
        }
        EditScript editScript = new SimplifiedChawatheScriptGenerator().computeActions(modified_mappings);
        tempSet.add(new Diff(gts.src, gts.dst, modified_mappings, editScript));
        return tempSet;
    }
}
