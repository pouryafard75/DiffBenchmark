package benchmark.metrics;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.SimplifiedChawatheScriptGenerator;
import com.github.gumtreediff.gen.jdt.JdtTreeGenerator;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.MappingStore;
import com.github.gumtreediff.tree.Tree;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static benchmark.utils.Configuration.REPOS;
import static benchmark.utils.Helpers.runWhatever;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;


/* Created by pourya on 2023-04-02 7:49 p.m. */
public class executionTime {
    public static void main(String[] args) throws Exception {
        int mm = 0;
        Configuration config = Configuration.ConfigurationFactory.getDefault();
        List<Long> rmTimes = new ArrayList<>();
        List<Long> gtgTimes = new ArrayList<>();
        List<Long> gtsTimes = new ArrayList<>();
        int i = 0;
        List<CaseInfo> temp5copies = new ArrayList();
        config.getAllCases().forEach(caseInfo -> {
            for (int j = 0; j < 5; j++) {
                temp5copies.add(caseInfo);
            }
        });
        for (CaseInfo info : temp5copies) {
//            if (i == 100) break;
            i ++;
            long RM_time = Integer.MAX_VALUE;
            if (info.getRepo().contains(".git")) {
                GitService gitService = new GitServiceImpl();
                String repoFolder = info.getRepo().substring(info.getRepo().lastIndexOf("/"), info.getRepo().indexOf(".git"));
                Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, info.getRepo());
                RM_time = new GitHistoryRefactoringMinerImpl().diffTime(repository, info.getCommit());
            }
            else {
                Path beforePath = Path.of(getBeforeDir(info.getRepo(), info.getCommit()));
                Path afterPath = Path.of(getAfterDir(info.getRepo(), info.getCommit()));
                RM_time = new GitHistoryRefactoringMinerImpl().diffTime
                        (beforePath,afterPath);
            }
            long GTG_time = 0;
            long GTS_time = 0;
            long start, finish;
            MappingStore match;
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
                String srcContents = projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath());
                String dstContents = projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath());
                //
                start = System.currentTimeMillis();
                Tree srcTree = new JdtTreeGenerator().generateFrom().string(srcContents).getRoot();
                Tree dstTree = new JdtTreeGenerator().generateFrom().string(dstContents).getRoot();
                match = new CompositeMatchers.ClassicGumtree().match(srcTree, dstTree);
                new SimplifiedChawatheScriptGenerator().computeActions(match);
                finish = System.currentTimeMillis();
                GTG_time += finish - start;
                //
                start = System.currentTimeMillis();
                srcTree = new JdtTreeGenerator().generateFrom().string(srcContents).getRoot();
                dstTree = new JdtTreeGenerator().generateFrom().string(dstContents).getRoot();
                match = new CompositeMatchers.SimpleGumtree().match(srcTree, dstTree);
                new SimplifiedChawatheScriptGenerator().computeActions(match);
                finish = System.currentTimeMillis();
                GTS_time += finish - start;
            }
            rmTimes.add(RM_time);
            gtgTimes.add(GTG_time);
            gtsTimes.add(GTS_time);

        }
        System.out.println(rmTimes);
        System.out.println(gtgTimes);
        System.out.println(gtsTimes);
    }


}
