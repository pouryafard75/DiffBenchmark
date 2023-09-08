package benchmark.metrics;

import benchmark.utils.CaseInfo;
import benchmark.utils.ChurnCalculator;
import benchmark.utils.Configuration;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gui.webdiff.DirComparator;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static benchmark.utils.ChurnCalculator.*;
import static benchmark.utils.Helpers.make;

/* Created by pourya on 2023-08-30 8:54 p.m. */
public class DatasetDetailedChurnToCSV {
    private static final boolean includeAddedAndRemovedFiles = true;
    //url addedInModifiedFiles deletedFromModifiedFiles sizeOfModifiedFilesNew sizeOfModifiedFilesOld addedFilesSize removedFilesSize
    public static void main(String[] args) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        ArrayList<String> lines = new ArrayList<>();
        int i = 0;
        String delimiter = "\t";
        for (CaseInfo info : Configuration.ConfigurationFactory.current().allCases) {
            ProjectASTDiff projectASTDiff = make(info.getRepo(), info.getCommit());
//            if (projectASTDiff.getDiffSet().size() > 2 ) continue;
            String url = info.makeURL();
            int addedInModifiedFiles = 0;
            int deletedFromModifiedFiles = 0;
            int sizeOfModifiedFilesNew = 0;
            int sizeOfModifiedFilesOld = 0;
            int addedFilesSize = 0;
            int removedFilesSize = 0;
            DirComparator dirComparator = new DirComparator(projectASTDiff);
            Pair<Integer, Integer> churn = Pair.of(0, 0);
            Pair<Integer, Integer> size = Pair.of(0, 0);
            for (ASTDiff diff : projectASTDiff.getDiffSet()) {
                String oldContent = projectASTDiff.getFileContentsBefore().get(diff.getSrcPath());
                String newContent = projectASTDiff.getFileContentsAfter().get(diff.getDstPath());
                churn = addPair(churn, calculateAddDeleteChurn(oldContent, newContent));
                size = addPair(size,Pair.of(getLines(oldContent).size(), getLines(newContent).size()));
            }
            addedInModifiedFiles = churn.getLeft();
            deletedFromModifiedFiles = churn.getRight();
            sizeOfModifiedFilesOld = size.getLeft();
            sizeOfModifiedFilesNew = size.getRight();

            for (String s : dirComparator.getRemovedFilesName()) {
                removedFilesSize += getLines(projectASTDiff.getFileContentsBefore().get(s)).size();
            }
            for (String s : dirComparator.getAddedFilesName()) {
                addedFilesSize += getLines(projectASTDiff.getFileContentsAfter().get(s)).size();
            }

            String line = url + delimiter + addedInModifiedFiles + delimiter + deletedFromModifiedFiles + delimiter + sizeOfModifiedFilesNew + delimiter + sizeOfModifiedFilesOld + delimiter + addedFilesSize + delimiter + removedFilesSize;
            lines.add(line);
            System.out.println(line);
            i ++ ;
//            if (i == 4) break;
        }
        writeArrayListToFile(lines, "detailed-churn.csv");
    }
    private static void writeArrayListToFile(ArrayList<String> lines, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // Add a newline character after each line
            }
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
}
