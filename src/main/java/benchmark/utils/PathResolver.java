package benchmark.utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;

/* Created by pourya on 2023-04-17 7:29 p.m. */
public class PathResolver {
    public static String getCommonPath(String baseFolder, String fileName, String toolname, String commit, String repo) {
        String replacedFileName = fileNameAsFolder(fileName);
        String repoReplaced = repoFolder(repo);
        return baseFolder + repoReplaced + "/" + commit + "/" + replacedFileName + "/" + toolname + ".json";
    }

    /**
     * We have to replace the old logic with the invocation of this method
     */
    public static String removeJavaExtension(String fileName) {
        String toRemove = ".java";
        if (fileName.endsWith(toRemove)) {
            // Remove the .java extension
            return fileName.substring(0, fileName.length() - toRemove.length());
        } else {
            // If .java is not at the end, return the original string
            return fileName;
        }
    }
    /**
     * Be careful on the usage of this method, might remove the .java in the name of the file
     */
    public static String fileNameAsFolder(String srcPath) {
        return srcPath.replace(".java", "").replace("/", ".");
    }

    public static String repoFolder(String repo) {
        return repo.replace("https://github.com/", "").replace(".git", "").replace("/", ".");
    }

    public static String getAfterDir(String projectDir, String bugID) {
        return getProperDir("after",projectDir,bugID);
    }

    public static String getBeforeDir(String projectDir, String bugID) {
        return getProperDir("before",projectDir,bugID);
    }

    private static String getProperDir(String prefix, String projectDir, String bugID) {
        String dir = getDefect4jDir();
        return dir + prefix + File.separator + projectDir + File.separator + bugID;
    }

    public static String getDefect4jDir() {
        return ORACLE_DIR + "defects4j" + File.separator;
    }

    public static List<Path> getPaths(Path dir, int exactDepth) throws IOException {
        int minDepth = exactDepth;
        int maxDepth = minDepth + 1;
        return Files.walk(dir, maxDepth)
                .filter(path -> path.toFile().isDirectory())
                .filter(path -> path.getNameCount() - dir.getNameCount() >= minDepth)
                .collect(Collectors.toList());
    }
    public static String exportedFolderPathByCaseInfo(CaseInfo info) {
        return repoFolder(info.getRepo()) +  "/" + info.getCommit();
    }
}
