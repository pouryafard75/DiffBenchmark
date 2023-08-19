package benchmark.oracle.generators;

import benchmark.oracle.generators.changeAPI.IJM;
import benchmark.oracle.generators.changeAPI.MTDiff;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration;
import com.github.gumtreediff.matchers.*;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.refactoringminer.api.GitService;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.actions.SimplifiedChawatheScriptGenerator;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;
import org.refactoringminer.util.GitServiceImpl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static benchmark.utils.Configuration.REPOS;
import static benchmark.utils.PathResolver.getAfterDir;
import static benchmark.utils.PathResolver.getBeforeDir;

/* Created by pourya on 2023-02-08 3:00 a.m. */
public class BenchmarkHumanReadableDiffGenerator {

    private Map<String, String> fileContentsBefore;
    private Map<String, String> fileContentsCurrent;

    public BenchmarkHumanReadableDiffGenerator(){
    }
    public void generate(CaseInfo info) throws Exception {
        this.writeActiveTools(info);
    }
    private void writeActiveTools(CaseInfo info) throws Exception {
        String repo = info.getRepo();
        String commit = info.getCommit();
        ProjectASTDiff projectASTDiff;
        Set<ASTDiff> astDiffs;
        if (repo.contains("github")) {
            GitService gitService = new GitServiceImpl();
            String repoFolder = repo.substring(repo.lastIndexOf("/"), repo.indexOf(".git"));
            Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, repo);
//            astDiffs = new GitHistoryRefactoringMinerImpl().diffAtCommit(repository, commit);
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtCommit(repo, commit, 100);
            populateContents(repo,commit);
        }
        else{
            String projectDir = repo;
            String bugID = commit;
            Path beforePath = Path.of(getBeforeDir(projectDir, bugID));
            Path afterPath = Path.of(getAfterDir(projectDir, bugID));
            projectASTDiff = new GitHistoryRefactoringMinerImpl().diffAtDirectories(
                    beforePath, afterPath);
            populateContents(beforePath.toFile(),afterPath.toFile());
        }
        astDiffs = projectASTDiff.getDiffSet();
        boolean succeed = false;
        for (ASTDiff astDiff : astDiffs) {
            try {
                HumanReadableDiffGenerator perfectHDG =
                        new HumanReadableDiffGenerator(repo, commit, new PerfectDiff(astDiff.getSrcPath(),astDiffs,repo,commit).makeASTDiff(), fileContentsBefore, fileContentsCurrent);
                perfectHDG.write(Configuration.GOD_PATH,astDiff.getSrcPath());
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("RMD")) { //This must be always active
                    HumanReadableDiffGenerator rmHDG = new HumanReadableDiffGenerator(repo, commit, astDiff, fileContentsBefore, fileContentsCurrent);
                    rmHDG.write(Configuration.RMD_PATH, astDiff.getSrcPath());
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("GTG")) {
                    HumanReadableDiffGenerator gtgHDG =
                            new HumanReadableDiffGenerator(repo, commit, makeASTDiffFromMatcher(new CompositeMatchers.ClassicGumtree(), astDiff), fileContentsBefore, fileContentsCurrent);
                    gtgHDG.write(Configuration.GTG_PATH, astDiff.getSrcPath());
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("GTS")) {
                    HumanReadableDiffGenerator gtsHDG =
                            new HumanReadableDiffGenerator(repo, commit, makeASTDiffFromMatcher(new CompositeMatchers.SimpleGumtree(), astDiff), fileContentsBefore, fileContentsCurrent);
                    gtsHDG.write(Configuration.GTS_PATH, astDiff.getSrcPath());
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("IJM")) {
                    HumanReadableDiffGenerator ijmHDG =
                            new HumanReadableDiffGenerator(repo, commit, new IJM(projectASTDiff,astDiff).makeASTDiff(), fileContentsBefore, fileContentsCurrent);
                    ijmHDG.write(Configuration.IJM_PATH, astDiff.getSrcPath());
                }
                //----------------------------------\\
                if (Configuration.toolPathMap.containsKey("MTD")) {
                    HumanReadableDiffGenerator mtdHDG =
                            new HumanReadableDiffGenerator(repo, commit, new MTDiff(projectASTDiff,astDiff).makeASTDiff(), fileContentsBefore, fileContentsCurrent);
                    mtdHDG.write(Configuration.MTD_PATH, astDiff.getSrcPath());
                }
                succeed = true;
            }
//            catch (APIChangerException apiChangerException)
//            {
////                System.out.println(apiChangerException.getMessage());
//                succeed = false;
//            }
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
        System.out.println("Finished for " + repo + " " + commit);
    }

    private void populateContents(String repo, String commitId) throws Exception {
        GitService gitService = new GitServiceImpl();
        String repoFolder = repo.substring(repo.lastIndexOf("/"), repo.indexOf(".git"));
        Repository repository = gitService.cloneIfNotExists(REPOS + repoFolder, repo);
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(commitId));
            if (commit.getParentCount() > 0) {
                walk.parseCommit(commit.getParent(0));
                Set<String> filePathsBefore = new LinkedHashSet<String>();
                Set<String> filePathsCurrent = new LinkedHashSet<String>();
                Map<String, String> renamedFilesHint = new HashMap<String, String>();
                gitService.fileTreeDiff(repository, commit, filePathsBefore, filePathsCurrent, renamedFilesHint);

                Set<String> repositoryDirectoriesBefore = new LinkedHashSet<String>();
                Set<String> repositoryDirectoriesCurrent = new LinkedHashSet<String>();
                Map<String, String> fileContentsBefore = new LinkedHashMap<String, String>();
                Map<String, String> fileContentsCurrent = new LinkedHashMap<String, String>();
                if (!filePathsBefore.isEmpty() && !filePathsCurrent.isEmpty() && commit.getParentCount() > 0) {
                    RevCommit parentCommit = commit.getParent(0);
                    GitHistoryRefactoringMinerImpl.populateFileContents(repository, parentCommit, filePathsBefore, fileContentsBefore, repositoryDirectoriesBefore);
                    GitHistoryRefactoringMinerImpl.populateFileContents(repository, commit, filePathsCurrent, fileContentsCurrent, repositoryDirectoriesCurrent);
                }
                this.fileContentsBefore = fileContentsBefore;
                this.fileContentsCurrent = fileContentsCurrent;
            }
        }
    }
    private void populateContents(File previousFile, File nextFile){
        Map<String, String> fileContentsBefore = new LinkedHashMap<String, String>();
        Map<String, String> fileContentsCurrent = new LinkedHashMap<String, String>();
        if(previousFile.exists() && nextFile.exists()) {
            try {
                if(previousFile.isDirectory() && nextFile.isDirectory()) {
                    Set<String> repositoryDirectoriesBefore = new LinkedHashSet<String>();
                    Set<String> repositoryDirectoriesCurrent = new LinkedHashSet<String>();
                    populateFileContents(nextFile, getJavaFilePaths(nextFile), fileContentsCurrent, repositoryDirectoriesCurrent);
                    populateFileContents(previousFile, getJavaFilePaths(previousFile), fileContentsBefore, repositoryDirectoriesBefore);

                }
                else if(previousFile.isFile() && nextFile.isFile()) {
                    String previousFileName = previousFile.getName();
                    String nextFileName = nextFile.getName();
                    if(previousFileName.endsWith(".java") && nextFileName.endsWith(".java")) {
                        Set<String> repositoryDirectoriesBefore = new LinkedHashSet<String>();
                        Set<String> repositoryDirectoriesCurrent = new LinkedHashSet<String>();
                        populateFileContents(nextFile.getParentFile(), List.of(nextFileName), fileContentsCurrent, repositoryDirectoriesCurrent);
                        populateFileContents(previousFile.getParentFile(), List.of(previousFileName), fileContentsBefore, repositoryDirectoriesBefore);
                    }
                }
            }
            catch (Exception e) {
            }
        }
        this.fileContentsBefore = fileContentsBefore;
        this.fileContentsCurrent = fileContentsCurrent;
    }
    private static List<String> getJavaFilePaths(File folder) throws IOException {
        Stream<Path> walk = Files.walk(Paths.get(folder.toURI()));
        List<String> paths = walk.map(x -> x.toString())
                .filter(f -> f.endsWith(".java"))
                .map(x -> x.substring(folder.getPath().length()+1).replaceAll(systemFileSeparator, "/"))
                .collect(Collectors.toList());
        walk.close();
        return paths;
    }
    private static void populateFileContents(File projectFolder, List<String> filePaths, Map<String, String> fileContents,	Set<String> repositoryDirectories) throws IOException {
        for(String path : filePaths) {
            String fullPath = projectFolder + File.separator + path.replaceAll("/", systemFileSeparator);
            String contents = FileUtils.readFileToString(new File(fullPath));
            fileContents.put(path, contents);
            String directory = new String(path);
            while(directory.contains("/")) {
                directory = directory.substring(0, directory.lastIndexOf("/"));
                repositoryDirectories.add(directory);
            }
        }
    }
    private static final String systemFileSeparator = Matcher.quoteReplacement(File.separator);

    private static ASTDiff makeASTDiffFromMatcher(CompositeMatchers.CompositeMatcher matcher, ASTDiff astDiff) {
        MappingStore match = matcher.match(astDiff.src.getRoot(), astDiff.dst.getRoot());
        ExtendedMultiMappingStore mappingStore = new ExtendedMultiMappingStore(astDiff.src.getRoot(), astDiff.dst.getRoot());
        mappingStore.add(match);
        ASTDiff diff = new ASTDiff(astDiff.getSrcPath(), astDiff.getDstPath(), astDiff.src, astDiff.dst, mappingStore);
        if (diff.getAllMappings().size() != match.size())
            if (!astDiff.getSrcPath().equals("src_java_org_apache_commons_lang_math_NumberUtils.java"))
                throw new RuntimeException("Mapping has been lost!");
        return diff;
    }
}
