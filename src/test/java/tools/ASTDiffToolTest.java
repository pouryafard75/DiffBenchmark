package tools;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;

import java.io.File;
import java.io.IOException;
import java.util.stream.Stream;

import static benchmark.utils.Helpers.runWhatever;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/* Created by pourya on 2024-05-03*/
public class ASTDiffToolTest {

    private static ProjectASTDiff projectASTDiff;
    private static ASTDiff target;
    private static CaseInfo info;
    private static Configuration conf;

    @BeforeAll
    public static void setup() {
        info = new CaseInfo("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825");
        projectASTDiff = runWhatever(info);
        target = projectASTDiff.getDiffSet().iterator().next();
        conf = ConfigurationFactory.refOracle();
    }
    public static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(ASTDiffTool.GOD,255)
               ,Arguments.of(ASTDiffTool.RMD,255)
               ,Arguments.of(ASTDiffTool.GTG,234)
               ,Arguments.of(ASTDiffTool.GTS,220)
               ,Arguments.of(ASTDiffTool.IJM,126)
               ,Arguments.of(ASTDiffTool.MTD,216)
               ,Arguments.of(ASTDiffTool.GT2,218)
               ,Arguments.of(ASTDiffTool.IAM,228)
               ,Arguments.of(ASTDiffTool.RM2,219)
               ,Arguments.of(ASTDiffTool.TRV,25)
               ,Arguments.of(ASTDiffTool.VNG,234)
               ,Arguments.of(ASTDiffTool.VNS,220)
               ,Arguments.of(ASTDiffTool.SMG,235)
               ,Arguments.of(ASTDiffTool.SMS,220)
               ,Arguments.of(ASTDiffTool.NMG,243)
               ,Arguments.of(ASTDiffTool.NMS,273)
               ,Arguments.of(ASTDiffTool.FLG,234)
               ,Arguments.of(ASTDiffTool.FLS,220)
               ,Arguments.of(ASTDiffTool.FTG,224)
               ,Arguments.of(ASTDiffTool.FTS,217)
//               ,Arguments.of(ASTDiffTool.ALG,0)
//               ,Arguments.of(ASTDiffTool.ALS,0)
        );
    }
    @ParameterizedTest
    @MethodSource("provideData")
    public void testNumOfMappings(ASTDiffTool diffTool, int numOfMappings) {
        ASTDiff result = null;
        try {
            result = diffTool.diff(projectASTDiff, target, info, conf);
        } catch (Exception e) {
            fail();
        }
//        try {
//            MappingExportModel.exportToFile(new File(diffTool.name() + ".json"), result.getAllMappings().getMappings());
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
        int size = result.getAllMappings().size();
        System.out.println(size);
        assertEquals(numOfMappings, size, "Number of mappings is not correct");
    }

}
