package benchmark.metrics;

import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;

import java.util.LinkedHashMap;
import java.util.Map;

import static benchmark.oracle.generators.tools.models.ASTDiffTool.GOD;
import static benchmark.oracle.generators.tools.models.ASTDiffTool.GTG;
import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-09-19 6:18 p.m. */
public class MetaInformationCalculator {
    public static void main(String[] args) throws Exception {
        run();
    }
    public static void run() throws Exception {
        ASTDiffTool[] experiment_tools = new ASTDiffTool[]{GTG};
        Configuration configuration = ConfigurationFactory.defects4j();
        Map<String, Integer> violationNumberPerParentsMap = new LinkedHashMap<>();
        int mm = 0;
        int ref = 0;
        int numOfFiles = 0;
        for (CaseInfo info : configuration.getAllCases()) {
//            if (numOfFiles > 10) break;
            System.out.println("Working on " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = runWhatever( info.getRepo(),  info.getCommit());
//            if (projectASTDiff.getRefactoringList().size() > 0) ref ++;
            numOfFiles += projectASTDiff.getDiffSet().size();
            for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet()) {
                ASTDiff perfect = GOD.getFactory().getASTDiff(projectASTDiff, rm_astDiff, info, configuration);
                if (perfect.getAllMappings().dstToSrcMultis().size() > 0
                    ||
                    perfect.getAllMappings().srcToDstMultis().size() > 0)
                {
                    mm ++ ;
                    break;
                }
            }
        }
        System.out.println("Contains multi-mappings: " + mm);
        System.out.println("Contains refactorings: " + ref);
        System.out.println("Number of files: " + numOfFiles);

    }
}
