package benchmark.oracle.generators;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.github.gumtreediff.matchers.*;
import org.eclipse.jgit.lib.Repository;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.util.Set;

import static benchmark.utils.Configuration.REPOS;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {
    public BenchmarkHumanReadableDiffGenerator(){

    }
    public void writeToFiles(CaseInfo info) throws Exception {
        String repo = info.getRepo();
        String commit = info.getCommit();
        this.writeToFiles(repo, commit);
    }
    private void writeToFiles(String repo, String commit) throws Exception {
        GitService gitService = new GitServiceImpl();String repoFolder = repo.substring(repo.lastIndexOf("/"), repo.indexOf(".git"));
        Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, repo);
        Set<ASTDiff> astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
        for (ASTDiff astDiff : astDiffs) {
            HumanReadableDiffGenerator refactoringMinerHDG = new HumanReadableDiffGenerator(repo, commit, astDiff);
            refactoringMinerHDG.make();
            refactoringMinerHDG.write(Configuration.RMD_PATH);
            HumanReadableDiffGenerator gtgHD = oracleGeneratorFromMappingStore(new CompositeMatchers.ClassicGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot()), repo, commit, astDiff);
            gtgHD.write(Configuration.GTG_PATH);
            HumanReadableDiffGenerator gtsHD = oracleGeneratorFromMappingStore(new CompositeMatchers.SimpleGumtree().match(astDiff.src.getRoot(), astDiff.dst.getRoot()), repo, commit, astDiff);
            gtsHD.write(Configuration.GTS_PATH);
            //TODO: make IJM and MTDiff work + check the active tools
        }
    }

    private static HumanReadableDiffGenerator oracleGeneratorFromMappingStore(MappingStore match, String repo, String commit, ASTDiff astDiff) {
        ExtendedMultiMappingStore GTG_mappingStore = new ExtendedMultiMappingStore(astDiff.src.getRoot(), astDiff.dst.getRoot());
        GTG_mappingStore.add(match);
        ASTDiff diff = new ASTDiff(astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.src, astDiff.dst, GTG_mappingStore);
        diff.setSrcContents(astDiff.getSrcContents());
        diff.setDstContents(astDiff.getDstContents());
        HumanReadableDiffGenerator toolHD = new HumanReadableDiffGenerator(repo, commit, diff);
        toolHD.make();
        return toolHD;
    }
}