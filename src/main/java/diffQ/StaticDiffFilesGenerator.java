package diffQ;

import benchmark.data.diffcase.QCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import gui.webdiff.WebDiff;
import gui.webdiff.export.WebExporter;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.*;

import static java.lang.Thread.sleep;

public class StaticDiffFilesGenerator{
    static Map<QCase, Integer> generate(Certificate certificate) throws Exception {
        System.out.println("Generating static diff files for certificate: " + certificate.config.qid);
        Map<QCase, Integer> filteredCounts = new LinkedHashMap<>();
        for (Map.Entry<QCase, Map<String, ASTDiffToolEnum>> stringMapEntry : certificate.cle.entrySet()) {
            System.out.println("Processing QCase: " + stringMapEntry.getKey().getID());
            QCase qcase = stringMapEntry.getKey();
            ProjectASTDiff projectASTDiff = qcase.getProjectASTDiff();
            Map<String, ASTDiffToolEnum> tools = stringMapEntry.getValue();
            Map<String, ProjectASTDiff> originalPads = new LinkedHashMap<>();
            for (Map.Entry<String, ASTDiffToolEnum> stringASTDiffToolEnumEntry : tools.entrySet()) {
                String alias = stringASTDiffToolEnumEntry.getKey();
                ASTDiffToolEnum tool = stringASTDiffToolEnumEntry.getValue();
                ProjectASTDiff toolPAD = Utils.makeToolsProjectASTDiff(qcase, projectASTDiff, tool);
                originalPads.put(alias, toolPAD);
            }

            Map<String, ProjectASTDiff> pads = originalPads;
//            Map<String, ProjectASTDiff> pads = new MyPadFilterer(qcase).filter(originalPads);
//            filteredCounts.put(qcase, pads.values().iterator().next().getDiffSet().size());

            int basePort = 9000;
            int counter = 0;
            for (Map.Entry<String, ASTDiffToolEnum> stringASTDiffToolEnumEntry : tools.entrySet()) {
                counter += 1;
                String alias = stringASTDiffToolEnumEntry.getKey();
                ProjectASTDiff toolPAD = pads.get(alias);
                //Assign random port to each tool, start from somewhere safe to ensure no conflicts
                int port = basePort + counter;
                WebDiff webDiff = new WebDiff(toolPAD);
                webDiff.setPort(port);
                webDiff.setToolName(alias);
                webDiff.run();
                WebExporter webExporter = new WebExporter(webDiff);
                webExporter.setViewers_path(Set.of("monaco-minimal"));
                webExporter.setOtherPages(Set.of());
                String pid = certificate.config.prs.get(qcase);
                String outputDir = DiffQDriver.dir + "/exports/" + certificate.config.qid + "/" + pid;
                webExporter.export(outputDir + "/" + alias);
                webDiff.terminate();
                sleep(1000); // Sleep to ensure the server is properly terminated before the next iteration
            }
        }
        return filteredCounts;
    }

    private static void printInfo(Map<String, ProjectASTDiff> pads) {
        for (Map.Entry<String, ProjectASTDiff> entry : pads.entrySet()) {
            String tool = entry.getKey();
            ProjectASTDiff pad = entry.getValue();
            System.out.print("Tool: " + tool + ", Diffs: " + pad.getDiffSet().size() + "----" );
        }
        System.out.println();
    }
}

