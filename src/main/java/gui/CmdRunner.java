package gui;

import benchmark.gui.web.BenchmarkWebDiffFactory;
import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import gui.webdiff.WebDiff;
import org.refactoringminer.astDiff.utils.URLHelper;
import org.refactoringminer.rm1.GitHistoryRefactoringMinerImpl;

import java.io.File;

import static benchmark.utils.Configuration.ConfigurationFactory.ORACLE_DIR;

public class CmdRunner {

    private enum Action {
        RUN, COMPARE
    }

    @Parameter(names = {"--url", "-u"}, description = "URL to compare")
    private String url;

//    @Parameter(names = {"--repo", "-r"}, description = "Repository name")
//    private String repo;
//
//    @Parameter(names = {"--commit", "-c"}, description = "Commit ID")
//    private String commitId;

    @Parameter(names = {"--srcDir"}, description = "First directory path")
    private String srcDir;

    @Parameter(names = {"--dstDir"}, description = "Second directory path")
    private String dstDir;

    @Parameter(names = {"--file1"}, description = "First file path")
    private String filePath1;
//
    @Parameter(names = {"--file2"}, description = "Second file path")
    private String filePath2;

    @Parameter(names = {"--action", "-a"}, description = "Action to perform (RUN or COMPARE)")
    private Action action;

    @Parameter(names = {"--help", "-h"}, description = "Show help", help = true)
    private boolean help;

    public static void main(String[] args) throws Exception {
        CmdRunner runner = new CmdRunner();
        JCommander commander =JCommander.newBuilder()
                .addObject(runner)
                .build();
	commander.parse(args);

	if (runner.help) commander.usage(); // Display help
	else runner.run();
    }

    void run() throws Exception {
        if (url != null) {
            if ((action == Action.COMPARE)) {
                new WebDiff(
                        new GitHistoryRefactoringMinerImpl().diffAtCommitWithGitHubAPI
                                (URLHelper.getRepo(url), URLHelper.getCommit(url), new File(ORACLE_DIR)))
                        .run();
            } else {
                BenchmarkWebDiffFactory.withURL(url).run();
            }
        }
        else if (srcDir != null && dstDir != null) {
            if ((action == Action.COMPARE)) {
                new WebDiff(
                        new GitHistoryRefactoringMinerImpl().diffAtDirectories
                                (new File(srcDir).toPath(), new File(dstDir).toPath()))
                        .run();
            } else {
                BenchmarkWebDiffFactory.withTwoDirectories(srcDir, dstDir).run();
            }
        }
        else if (filePath1 != null && filePath2 != null) {
            if ((action == Action.COMPARE)) {
                new WebDiff(
                        new GitHistoryRefactoringMinerImpl().diffAtDirectories(
                                new File(filePath1), new File(filePath2)))
                        .run();
            } else {
                BenchmarkWebDiffFactory.withTwoDirectories(filePath1, filePath2).run();
            }
        }
        else {
            System.out.println("Please provide a valid input. Use --help to see the options");
        }
    }
}
