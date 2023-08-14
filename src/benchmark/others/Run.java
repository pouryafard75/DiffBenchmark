package benchmark.others;

import benchmark.defects4j.CompareMappings;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static benchmark.defects4j.CompareMappings.*;

/* Created by pourya on 2023-06-01 11:58 p.m. */
public class Run {
    public static final String INPUT_PATH;

    public static final String BEARS = "bears";
    public static final String VUL4J = "vul4j";
    public static final String DATASET = BEARS;

    static {
        INPUT_PATH = "datasets/" + DATASET + "/" + "commits-url.txt";
    }

    public static void main(String[] args) throws IOException {
        int[] arr = new int[]{1,2,3,4,5,6};
        List<Integer> mylist = List.of();
//        extracted();

    }

    private static void extracted() throws IOException {
        List<String> urls = readFile();
        int i = 0;
        for (String url : urls) {
//            System.out.println(url);
            i += 1;
            ProjectASTDiff projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(URLHelper.getRepo(url), URLHelper.getCommit(url), 1000);
            Set<ASTDiff> diffSet = projectASTDiff.getDiffSet();
            boolean _SameWithGreedy = isAllIdentical(diffSet, GREEDY);
            boolean _SameWithSimple = isAllIdentical(diffSet, SIMPLE);
            boolean _SameWithAny = _SameWithGreedy || _SameWithSimple;
            if (!_SameWithAny)
                System.out.println(i + ": Different");
            else {
//                System.out.println(i + ": Same with num of diff files: " + diffSet.size());
            }
        }
    }

    public static List<String> readFile() throws IOException {
        List<String> urls = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(INPUT_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                urls.add(line);
            }
        }
        return urls;
    }
}
