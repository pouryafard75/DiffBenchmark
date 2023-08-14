package benchmark.defects4j;

import benchmark.utils.CaseInfo;
import benchmark.utils.PathResolver;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static benchmark.defects4j.CompareMappings.getDirPathList;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2023-05-24 1:02 p.m. */
public class Snapshot {
    private final static org.slf4j.Logger logger = LoggerFactory.getLogger(Snapshot.class);
    private final static String snapshots = "snapshots";
    private final static String csvPath = "defect4j_issue_tracker - Validation.csv";

    public static void main(String[] args) throws IOException {
        List<CaseInfo> caseInfos = filterPerfects(readCSV(csvPath));
        make(caseInfos);

    }

    private static List<CaseInfo> filterPerfects(List<String[]> lines) {
        List<CaseInfo> cases = new ArrayList<>();
        int statusIndex = 3;
        String desiredString = "PerfectDiff";
        for (String[] line : lines) {
            if (line[statusIndex].equals(desiredString)){
                cases.add(new CaseInfo(line[0],line[1]));
            }
        }
        return cases;
    }

    public static List<String[]> readCSV(String filePath){
        List<String[]> lines = new ArrayList<>();
        String line = "";
        String splitBy = ",";
        try {
            //parsing a CSV file into BufferedReader class constructor
            BufferedReader br = new BufferedReader(new FileReader(filePath));
            while ((line = br.readLine()) != null)
            //returns a Boolean value
            {
                lines.add(line.split(splitBy));
            }
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        return lines;
    }


    private static void make(List<CaseInfo> caseInfos) throws IOException {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd_HH:mm:ss");
        String nowString = dtf.format(LocalDateTime.now());
        String baseOutputPath = snapshots + "/" + nowString + "/";
        new File(baseOutputPath).mkdir();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writerWithDefaultPrettyPrinter().writeValue(new File(baseOutputPath + "cases.json"),caseInfos);
        for (CaseInfo caseInfo : caseInfos) {
            String projectDir = caseInfo.getRepo();
            String bugID = caseInfo.getCommit();
            String beforeDir = getBeforeDir(projectDir,bugID);
            String afterDir = getAfterDir(projectDir,bugID);
            Set<ASTDiff> diffSet = new GitHistoryRefactoringMinerImpl().diffAtDirectories
                    (Path.of(beforeDir), Path.of(afterDir)).getDiffSet();
            for (ASTDiff astDiff : diffSet) {
                String outputDir = baseOutputPath + projectDir + "/" + bugID;
                Files.createDirectories(Paths.get(outputDir));
                String outputFile = outputDir + "/" + PathResolver.replaceFileName(astDiff.getSrcPath());
                MappingExportModel.exportToFile(new File(outputFile),astDiff.getAllMappings());
                logger.info(String.format("Mappings generated for %s", outputFile));
            }
        }
    }

}
