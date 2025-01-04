package benchmark;

import benchmark.data.dataset.Defects4JBenchmarkDataset;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.generators.tools.ASTDiffToolEnum;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.Constants;
import org.refactoringminer.astDiff.utils.MappingExportModel;
import org.refactoringminer.astDiff.utils.TreeUtilFunctions;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import static org.refactoringminer.astDiff.utils.ExportUtils.getFinalFilePath;
import static org.refactoringminer.astDiff.utils.ExportUtils.getFinalFolderPath;

/* Created by pourya on 2024-12-09*/
public class MigrationToNewVisitor {

    static String mappingsPath = "/Users/pourya/IdeaProjects/RM-ASTDiff/src/test/resources/astDiff/defects4j/";
    static Defects4JBenchmarkDataset dataset = new Defects4JBenchmarkDataset();


    public static void main(String[] args) throws Exception {
        System.out.println("Started correcting");
        System.out.println(dataset.getCases().size());
        for (IBenchmarkCase aCase : dataset.getCases()) {
            update(aCase);
        }
    }

    private static void update(IBenchmarkCase githubCase) throws Exception {
        ProjectASTDiff projectASTDiff = githubCase.getProjectASTDiff();
        for (ASTDiff rm : projectASTDiff.getDiffSet()) {
            ASTDiff god = ASTDiffToolEnum.GOD.diff(githubCase, (x) -> rm);
            for (Mapping thisMapping : rm.getAllMappings()) {
                Tree rmT1 = thisMapping.first;
                Tree rmT2 = thisMapping.second;
                String nameOfTM1 = findTheNameOfTheTree(rmT1, projectASTDiff, ProjectASTDiff::getParentContextMap);
                String nameOfTM2 = findTheNameOfTheTree(rmT2, projectASTDiff, ProjectASTDiff::getChildContextMap);
                if (nameOfTM1 == null || nameOfTM2 == null) {
                    throw new RuntimeException("Bug");
                }
                TreeContext tc1 = new JdtTreeGenerator().generateFrom().string(projectASTDiff.getFileContentsBefore().get(nameOfTM1));
                TreeContext tc2 = new JdtTreeGenerator().generateFrom().string(projectASTDiff.getFileContentsAfter().get(nameOfTM2));


                if (rmT1.getType().name.equals(Constants.COMPILATION_UNIT) ||
                    rmT2.getType().name.equals(Constants.COMPILATION_UNIT))
                    continue;
                Tree t1OldVisitor = TreeUtilFunctions.getTreeBetweenPositionsSecure(
                        tc1.getRoot(),
                        rmT1.getPos(),
                        rmT1.getEndPos(),
                        rmT1.getType().name,
                        rmT1.getParent().getType().name,
                        rmT1.getLabel()
                );
                Tree t2OldVisitor = TreeUtilFunctions.getTreeBetweenPositionsSecure(
                        tc2.getRoot(),
                        rmT2.getPos(),
                        rmT2.getEndPos(),
                        rmT2.getType().name,
                        rmT2.getParent().getType().name,
                        rmT2.getLabel()
                );
                if (t1OldVisitor == null && t2OldVisitor == null) {
//                    System.out.println(thisMapping);
                    Tree g1 = projectASTDiff.getParentContextMap().get(nameOfTM1).getRoot();
                    Tree g2 = projectASTDiff.getChildContextMap().get(nameOfTM2).getRoot();
                    Tree tg1 = TreeUtilFunctions.getTreeBetweenPositionsSecure(
                            g1,
                            rmT1.getPos(),
                            rmT1.getEndPos(),
                            rmT1.getType().name,
                            rmT1.getParent().getType().name,
                            rmT1.getLabel()
                    );
                    Tree tg2 = TreeUtilFunctions.getTreeBetweenPositionsSecure(
                            g2,
                            rmT2.getPos(),
                            rmT2.getEndPos(),
                            rmT2.getType().name,
                            rmT2.getParent().getType().name,
                            rmT2.getLabel()
                    );
                    if (tg1 == null || tg2 == null)
                        throw new RuntimeException("Bug");
                    else
                        god.getAllMappings().addMapping(tg1, tg2);
                }
            }
            //update god mappings in its file
            String finalPath = getFinalFilePath(god, mappingsPath,  githubCase.getRepo(), githubCase.getCommit());
            Files.createDirectories(Paths.get(getFinalFolderPath(mappingsPath,githubCase.getRepo(),githubCase.getCommit())));
            MappingExportModel.exportToFile(new File(finalPath), god.getAllMappings());
        }
        System.out.println("Finished correcting " + githubCase.getRepo() + " " + githubCase.getCommit());
    }

    private static String findTheNameOfTheTree(Tree t,
                                               ProjectASTDiff projectASTDiff,
                                               Function<ProjectASTDiff, Map<String, TreeContext>> getMap) {
        Map<String, TreeContext> childContextMap = getMap.apply(projectASTDiff);
        Set<Map.Entry<String, TreeContext>> entries = childContextMap.entrySet();
        for (Map.Entry<String, TreeContext> stringTreeContextEntry : entries) {
            TreeContext tc = stringTreeContextEntry.getValue();
            Tree query = TreeUtilFunctions.getTreeBetweenPositionsSecure(
                    tc.getRoot(),
                    t.getPos(),
                    t.getEndPos(),
                    t.getType().name,
                    (t.getParent() != null) ? t.getParent().getType().name : "",
                    t.getLabel()
            );
            if (query != null) return stringTreeContextEntry.getKey();
        }
        return null;
    }
}