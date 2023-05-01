package benchmark.oracle.generators;

import benchmark.oracle.generators.changeAPI.IJM;
import benchmark.oracle.generators.changeAPI.MTDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.github.gumtreediff.matchers.*;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.Set;

import static benchmark.utils.Configuration.REPOS;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {
    public BenchmarkHumanReadableDiffGenerator(){

    }
    public void generate(CaseInfo info) throws Exception {
        String repo = info.getRepo();
        String commit = info.getCommit();
        this.writeActiveTools(repo, commit);
    }
    private void writeActiveTools(String repo, String commit) throws Exception {
        GitService gitService = new GitServiceImpl();String repoFolder = repo.substring(repo.lastIndexOf("/"), repo.indexOf(".git"));
        Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, repo);
        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
        boolean succeed = false;
        for (ASTDiff astDiff : astDiffs) {
            try {
                if (Configuration.toolPathMap.containsKey("RMD")) { //This must be always active
                    HumanReadableDiffGenerator rmHDG = new HumanReadableDiffGenerator(repo, commit, astDiff);
                    rmHDG.write(Configuration.RMD_PATH);
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("GTG")) {
                    HumanReadableDiffGenerator gtgHDG =
                            new HumanReadableDiffGenerator(repo, commit, makeASTDiffFromMatcher(new CompositeMatchers.ClassicGumtree(), astDiff));
                    gtgHDG.write(Configuration.GTG_PATH);
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("GTS")) {
                    HumanReadableDiffGenerator gtsHDG =
                            new HumanReadableDiffGenerator(repo, commit, makeASTDiffFromMatcher(new CompositeMatchers.SimpleGumtree(), astDiff));
                    gtsHDG.write(Configuration.GTS_PATH);
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("IJM")) {
                    HumanReadableDiffGenerator ijmHDG =
                            new HumanReadableDiffGenerator(repo, commit, new IJM(astDiff).makeASTDiff());
                    ijmHDG.write(Configuration.IJM_PATH);
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("MTD")) {
                    HumanReadableDiffGenerator mtdHDG =
                            new HumanReadableDiffGenerator(repo, commit, new MTDiff(astDiff).makeASTDiff());
                    mtdHDG.write(Configuration.MTD_PATH);
                }
                succeed = true;
            }
            catch (APIChangerException apiChangerException)
            {
//                System.out.println(apiChangerException.getMessage());
                succeed = false;
            }
            finally {
                StringBuilder msg = new StringBuilder();
                if (succeed)
                    msg.append("Succeed");
                else
                    msg.append("Failed");
                msg.append(" for ").append(new CaseInfo(repo,commit).makeURL());
//                System.out.println(msg);
            }
            //----------------------------------\\

        }
    }

    private static ASTDiff makeASTDiffFromMatcher(CompositeMatchers.CompositeMatcher matcher, ASTDiff astDiff) {
        MappingStore match = matcher.match(astDiff.src.getRoot(), astDiff.dst.getRoot());
        ExtendedMultiMappingStore mappingStore = new ExtendedMultiMappingStore(astDiff.src.getRoot(), astDiff.dst.getRoot());
        mappingStore.add(match);
        ASTDiff diff = new ASTDiff(astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.src, astDiff.dst, mappingStore);
        if (diff.mappings.size() != match.size()) throw new RuntimeException("Mapping has been lost!");
        diff.setSrcContents(astDiff.getSrcContents());
        diff.setDstContents(astDiff.getDstContents());
        return diff;
    }
}
