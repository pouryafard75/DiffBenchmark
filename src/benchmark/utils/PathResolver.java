package benchmark.utils;

/* Created by pourya on 2023-04-17 7:29 p.m. */
public class PathResolver {
    public static String getFinalPath(String folderName, String fileName, String commit, String repo) {
        String replacedFileName = replaceFileName(fileName);
        String repoReplaced = repoFolder(repo);
        return folderName + repoReplaced + "/" + commit + "/" + replacedFileName;
    }

    public static String replaceFileName(String srcPath) {
        return srcPath.replace("/", ".").replace(".java", ".json");
    }

    public static String repoFolder(String repo) {
        return repo.replace("https://github.com/", "").replace(".git", "").replace("/", ".");
    }

    public static String getAfterDir(String projectDir, String bugID) {
        return getProperDir("before",projectDir,bugID);
    }

    public static String getBeforeDir(String projectDir, String bugID) {
        return getProperDir("after",projectDir,bugID);
    }

    private static String getProperDir(String prefix, String projectDir, String bugID) {
        String dir = getDefect4jDir();
        return dir + prefix + "/" + projectDir + "/" + bugID;
    }

    public static String getDefect4jDir() {
        String dir =  System.getProperty("user.dir") + "/datasets/defects4j/";
        return dir;
    }
}
