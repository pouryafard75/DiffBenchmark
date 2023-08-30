package benchmark.utils;


import com.github.difflib.DiffUtils;
import com.github.difflib.patch.*;
import gui.webdiff.DirComparator;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.util.*;

/* Created by pourya on 2023-08-30 12:07 p.m. */
public class ChurnCalculator {
    public static void main(String[] args) {
        String url = "https://github.com/pouryafard75/RM-ASTDiff/commit/c9ac82231cb927b34dfa74e6e0625c151a3acc41";
        url = "https://github.com/apache/cassandra/commit/2b0a8f6bdac621badabcb9921c077260f2470c26";
        url = "https://github.com/liferay/liferay-plugins/commit/7c7ecf4cffda166938efd0ae34830e2979c25c73";
        url = "https://github.com/JetBrains/intellij-community/commit/97811cf971f7ccf6a5fc5e90a491db2f58d49da1";
        ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(URLHelper.getRepo(url), URLHelper.getCommit(url), 1000);
        Pair<Integer, Integer> integerIntegerPair = calculateAddDeleteChurn(projectASTDiff);
        System.out.println(integerIntegerPair);
    }
    public static Pair<Integer, Integer> calculateAddDeleteChurn(String oldContent, String newContent) {
        List<String> oldLines = oldContent.equals("") ? new ArrayList<>() : Arrays.asList(oldContent.split("\n"));
        List<String> newLines = newContent.equals("") ? new ArrayList<>() : Arrays.asList(newContent.split("\n"));
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
    public static Pair<Integer,Integer> calculateAddDeleteChurn(ProjectASTDiff projectASTDiff) {

        DirComparator dirComparator = new DirComparator(projectASTDiff);
        Pair<Integer, Integer> result = Pair.of(0, 0);
        for (ASTDiff diff : projectASTDiff.getDiffSet()) {
            String oldContent = projectASTDiff.getFileContentsBefore().get(diff.getSrcPath());
            String newContent = projectASTDiff.getFileContentsAfter().get(diff.getDstPath());
            result = addPair(result,calculateAddDeleteChurn(oldContent, newContent));
        }
        for (String s : dirComparator.getAddedFilesName()) {
            String newContent = projectASTDiff.getFileContentsAfter().get(s);
            result = addPair(result,calculateAddDeleteChurn("", newContent));
        }
        for (String s : dirComparator.getRemovedFilesName()) {
            String oldContent = projectASTDiff.getFileContentsBefore().get(s);
            result = addPair(result,calculateAddDeleteChurn(oldContent, ""));
        }
        return result;
    }
    static Pair<Integer,Integer> addPair(Pair<Integer,Integer> p1, Pair<Integer,Integer> p2) {
        return Pair.of(p1.getLeft() + p2.getLeft(), p1.getRight() + p2.getRight());
    }
}
