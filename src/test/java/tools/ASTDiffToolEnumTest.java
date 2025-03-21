package tools;

import benchmark.data.diffcase.GithubCase;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/* Created by pourya on 2024-05-03*/
public class ASTDiffToolEnumTest {

    private static ProjectASTDiff projectASTDiff;
    private static ASTDiff target;
    private static IBenchmarkCase benchmarkCase;

    @BeforeAll
    public static void setup() {
        IBenchmarkCase info = new GithubCase("https://github.com/Alluxio/alluxio/commit/9aeefcd8120bb3b89cdb437d8c32d2ed84b8a825");
        target = projectASTDiff.getDiffSet().iterator().next();
    }
    public static Stream<Arguments> provideData() {
        return Stream.of(
                Arguments.of(ASTDiffToolEnum.GOD,255)
               ,Arguments.of(ASTDiffToolEnum.RMD,255)
               ,Arguments.of(ASTDiffToolEnum.GTG,234)
               ,Arguments.of(ASTDiffToolEnum.GTS,220)
               ,Arguments.of(ASTDiffToolEnum.IJM,126)
               ,Arguments.of(ASTDiffToolEnum.MTD,216)
               ,Arguments.of(ASTDiffToolEnum.GT2,218)
               ,Arguments.of(ASTDiffToolEnum.IAM,228)
               ,Arguments.of(ASTDiffToolEnum.RM2,219)
               ,Arguments.of(ASTDiffToolEnum.TRV,25)
               ,Arguments.of(ASTDiffToolEnum.VNG,234)
               ,Arguments.of(ASTDiffToolEnum.VNS,220)
               ,Arguments.of(ASTDiffToolEnum.SMG,235)
               ,Arguments.of(ASTDiffToolEnum.SMS,220)
               ,Arguments.of(ASTDiffToolEnum.NMG,243)
               ,Arguments.of(ASTDiffToolEnum.NMS,273)
//               ,Arguments.of(ASTDiffToolEnum.FLG,234)
//               ,Arguments.of(ASTDiffToolEnum.FLS,220)
               ,Arguments.of(ASTDiffToolEnum.FTG,224)
               ,Arguments.of(ASTDiffToolEnum.FTS,217)
//               ,Arguments.of(ASTDiffTool.ALG,0)
//               ,Arguments.of(ASTDiffTool.ALS,0)
        );
    }
    @ParameterizedTest
    @MethodSource("provideData")
    public void testNumOfMappings(ASTDiffToolEnum diffTool, int numOfMappings) {
        ASTDiff result = null;
        try {
            result = diffTool.diff(benchmarkCase, x -> target);
        } catch (Exception e) {
            fail();
        }
        int size = result.getAllMappings().size();
        System.out.println(size);
        assertEquals(numOfMappings, size, "Number of mappings is not correct");
    }

}
