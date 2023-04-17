package benchmark.utils;

/* Created by pourya on 2023-04-17 7:29 p.m. */
public class PathResoslver {

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
}
