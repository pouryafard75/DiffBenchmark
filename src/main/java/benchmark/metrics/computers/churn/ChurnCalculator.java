package benchmark.metrics.computers.churn;


import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.*;
import gui.webdiff.dir.DirComparator;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.util.*;

/* Created by pourya on 2023-08-30 12:07 p.m. */
public class ChurnCalculator {

    private static final String lineBreak = "\n";
    public static String makeGitDiffOutput(String oldFilePath, String newFilePath, String oldContent, String newContent){
        List<String> oldLines = getLines(oldContent);
        List<String> newLines = getLines(newContent);
        Patch<String> patch = DiffUtils.diff(oldLines, newLines);
        List<String> strings = UnifiedDiffUtils.generateUnifiedDiff(oldFilePath, newFilePath, oldLines, patch, 0);
        StringBuilder sb = new StringBuilder();
        for (String string : strings) {
            sb.append(string);
            sb.append("\n");
        }
        return sb.toString();
    }
    public static Pair<Integer, Integer> calculateAddDeleteChurn(String oldContent, String newContent) {
        List<String> oldLines = getLines(oldContent);
        List<String> newLines = getLines(newContent);
        Patch<String> patch = DiffUtils.diff(oldLines, newLines);
        int linesAdded = 0;
        int linesDeleted = 0;
//        int linesModified = 0;
        for (AbstractDelta<String> delta : patch.getDeltas()) {
            if (delta instanceof DeleteDelta) {
                Chunk<String> original = delta.getSource();
                linesDeleted += original.size();
            } else if (delta instanceof InsertDelta) {
                Chunk<String> revised = delta.getTarget();
                linesAdded += revised.size();
            }
            else if (delta instanceof ChangeDelta ) {
//                Chunk<String> revised = delta.getTarget();
//                    linesModified += revised.size();
                linesAdded += delta.getTarget().size();
                linesDeleted += delta.getSource().size();
            }
        }
        return Pair.of(linesAdded, linesDeleted);
    }

    public static List<String> getLines(String content) {
        return content.isEmpty() ? new ArrayList<>() : Arrays.asList(content.split(lineBreak));
    }

    public static Pair<Integer,Integer> calculateAddDeleteChurn(ProjectASTDiff projectASTDiff, boolean includeAddedFiles, boolean includeRemovedFiles) {

        DirComparator dirComparator = new DirComparator(projectASTDiff);
        Pair<Integer, Integer> result = Pair.of(0, 0);
        for (ASTDiff diff : projectASTDiff.getDiffSet()) {
            String oldContent = projectASTDiff.getFileContentsBefore().get(diff.getSrcPath());
            String newContent = projectASTDiff.getFileContentsAfter().get(diff.getDstPath());
            result = addPair(result, calculateAddDeleteChurn(oldContent, newContent));
        }
        if (includeAddedFiles) {
            for (String s : dirComparator.getAddedFilesName()) {
                String newContent = projectASTDiff.getFileContentsAfter().get(s);
                result = addPair(result, calculateAddDeleteChurn("", newContent));
            }
        }
        if (includeRemovedFiles) {
            for (String s : dirComparator.getRemovedFilesName()) {
                String oldContent = projectASTDiff.getFileContentsBefore().get(s);
                result = addPair(result, calculateAddDeleteChurn(oldContent, ""));
            }
        }
        return result;
    }

    public static Pair<Float, Float> calculateRelativeAddDeleteChurn(ProjectASTDiff projectASTDiff, boolean includeAddedFiles, boolean includeRemovedFiles) {

        Pair<Integer, Integer> churn = calculateAddDeleteChurn(projectASTDiff,includeAddedFiles,includeRemovedFiles);
        Pair<Integer, Integer> size = calculateAfterBeforeSize(projectASTDiff,includeAddedFiles,includeRemovedFiles);
        return Pair.of(
                (float)churn.getLeft() / (float)size.getLeft(),  ((float)churn.getRight() / (float)size.getRight()));
    }

    public static Pair<Integer, Integer> addPair (Pair < Integer, Integer > p1, Pair < Integer, Integer > p2){
        return Pair.of(p1.getLeft() + p2.getLeft(), p1.getRight() + p2.getRight());
    }

    public static Pair<Integer, Integer> calculateAfterBeforeSize(ProjectASTDiff projectASTDiff, boolean includeAddedFiles, boolean includeRemovedFiles) {
        DirComparator dirComparator = new DirComparator(projectASTDiff);
        Pair<Integer, Integer> result = Pair.of(0, 0);
        for (ASTDiff diff : projectASTDiff.getDiffSet()) {
            String oldContent = projectASTDiff.getFileContentsBefore().get(diff.getSrcPath());
            String newContent = projectASTDiff.getFileContentsAfter().get(diff.getDstPath());
            result = addPair(result,Pair.of(getLines(newContent).size(),getLines(oldContent).size()));
        }
        if (includeAddedFiles) {
            for (String s : dirComparator.getAddedFilesName()) {
                String newContent = projectASTDiff.getFileContentsAfter().get(s);
                String oldContent = "";
                result = addPair(result,Pair.of(getLines(newContent).size(),getLines(oldContent).size()));
            }
        }
        if (includeRemovedFiles) {
            for (String s : dirComparator.getRemovedFilesName()) {
                String newContent = "";
                String oldContent = projectASTDiff.getFileContentsBefore().get(s);
                result = addPair(result,Pair.of(getLines(newContent).size(),getLines(oldContent).size()));
            }
        }
        return result;
    }
    public static Pair<Integer, Integer> averageRelativeChurns(ProjectASTDiff projectASTDiff) {
        DirComparator dirComparator = new DirComparator(projectASTDiff);
        Pair<Integer, Integer> result = Pair.of(0, 0);
        List<Pair<Integer,Integer>> pairs = new ArrayList<>();
        for (ASTDiff diff : projectASTDiff.getDiffSet()) {
            String oldContent = projectASTDiff.getFileContentsBefore().get(diff.getSrcPath());
            String newContent = projectASTDiff.getFileContentsAfter().get(diff.getDstPath());
            pairs.add(singleFileRelativeChurn(newContent, oldContent));
        }
        for (String s : dirComparator.getAddedFilesName()) {
            String newContent = projectASTDiff.getFileContentsAfter().get(s);
            String oldContent = "";
            pairs.add(singleFileRelativeChurn(newContent, oldContent));
        }
        for (String s : dirComparator.getRemovedFilesName()) {
            String newContent = "";
            String oldContent = projectASTDiff.getFileContentsBefore().get(s);
            pairs.add(singleFileRelativeChurn(newContent, oldContent));
        }
        return result;
    }

    private static Pair<Integer, Integer> singleFileRelativeChurn(String newContent, String oldContent) {
        Pair<Integer, Integer> churn = calculateAddDeleteChurn(oldContent, newContent);
        Pair<Integer, Integer> size = Pair.of(getLines(newContent).size(), getLines(oldContent).size());
        return Pair.of(churn.getLeft() / size.getLeft(), churn.getRight() / size.getRight());
    }

    public static void main(String[] args) {
        String url = "https://github.com/spring-projects/spring-security/commit/fcc9a34356817d93c24b5ccf3107ec234a28b136";
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(URLHelper.getRepo(url), URLHelper.getCommit(url), 1000);
        Pair<Float, Float> res1 = calculateRelativeAddDeleteChurn(projectASTDiff, true, true);
        Pair<Float, Float> res2 = calculateRelativeAddDeleteChurn(projectASTDiff, false, false);
        System.out.println(res1);
        System.out.println(res2);
    }
}


