package benchmark.utils;

import java.io.File;

import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;

/* Created by pourya on 2023-04-17 7:29 p.m. */
public class PathResolver {
    public static String getCommonPath(String baseFolder, String fileName, String toolname, String commit, String repo) {
        String replacedFileName = fileNameAsFolder(fileName);
        String repoReplaced = repoFolder(repo);
        return baseFolder + repoReplaced + "/" + commit + "/" + replacedFileName + "/" + toolname + ".json";
    }
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
}