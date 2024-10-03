package benchmark.gui.web;

import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.gui.viewers.DiffViewers;
import benchmark.gui.conf.GuiConf;
import com.github.gumtreediff.utils.Pair;

import gui.webdiff.viewers.monaco.MonacoView;
import gui.webdiff.viewers.vanilla.VanillaDiffView;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;
import spark.Spark;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static spark.Spark.*;

;

public class BenchmarkWebDiff {
    public static final String JQUERY_JS_URL = "https://code.jquery.com/jquery-3.4.1.min.js";
    public static final String BOOTSTRAP_CSS_URL = "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css";
    public static final String BOOTSTRAP_JS_URL = "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js";
    public static final int port = 6868;

    private final ProjectASTDiff projectASTDiff;
    private final Map<IASTDiffTool, Set<ASTDiff>> diffs;
    private final GuiConf guiConf;


    public BenchmarkWebDiff(ProjectASTDiff projectASTDiffByRM, Map<IASTDiffTool, Set<ASTDiff>> diffs, GuiConf guiConf) {
        this.projectASTDiff = projectASTDiffByRM;
        this.diffs = diffs;
        this.guiConf = guiConf;
    }

    public static boolean isWindows() {
        String OS = System.getProperty("os.name").toLowerCase();
        return (OS.contains("win"));
    }
    public void run() {
        if (!isWindows()) {
            try {
                Runtime.getRuntime().exec("bash " + "kill.sh");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        BenchmarkDirComparator comperator = new BenchmarkDirComparator(projectASTDiff);
        configureSpark(comperator, this.port);
        Spark.awaitInitialization();
        System.out.println(String.format("Starting server: %s:%d.", "http://127.0.0.1", this.port));
    }

    public void configureSpark(final BenchmarkDirComparator comperator, int port) {
        port(port);
        staticFiles.location("/web/");
        get("/", (request, response) -> {
//            if (comparator.isDirMode())
            response.redirect("/list");
//            else
//                response.redirect("/monaco-diff/0");
            return "";
        });
        get("/list", (request, response) -> {
            Renderable view = new BenchmarkDirectoryDiffView(comperator, diffs, guiConf);
            return renderToString(view);
        });

        for (Map.Entry<IASTDiffTool, Set<ASTDiff>> astDiffToolSetEntry : diffs.entrySet()) {
            IASTDiffTool tool = astDiffToolSetEntry.getKey();
            Set<ASTDiff> astDiffs = astDiffToolSetEntry.getValue();
            for (DiffViewers enabledViewer : guiConf.enabled_viewers) {
                try {
                    enabledViewer.configure(tool, astDiffs, projectASTDiff);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }


        get("/left/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            Pair<String, String> pair = comperator.getFileContentsPair(id);
            return pair.first;
        });
        get("/right/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            Pair<String, String> pair = comperator.getFileContentsPair(id);
            return pair.second;
        });
        get("/quit", (request, response) -> {
            System.exit(0);
            return "";
        });
    }

    private void getMonaco(ASTDiffToolEnum tool, Set<ASTDiff> astDiffs, ProjectASTDiff projectASTDiff) {
        get("/" + tool + "-monaco/:id" , (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            ASTDiff astDiff = astDiffs.stream().toList().get(id);
            Renderable view = new MonacoView(tool.toString(), astDiff.getSrcPath(), astDiff.getDstPath(),
                    astDiff, id, projectASTDiff.getDiffSet().size(), "-monaco/", false);
            return renderToString(view);
        });
    }

    private static void getVanilla(ASTDiffToolEnum tool, Set<ASTDiff> astDiffs, ProjectASTDiff projectASTDiff) {
        get("/" + tool + "/:id" , (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            ASTDiff astDiff = astDiffs.stream().toList().get(id);
            Renderable view = new VanillaDiffView(tool.toString(), astDiff.getSrcPath(), astDiff.getDstPath(),
                    astDiff, id, projectASTDiff.getDiffSet().size(), "-vanilla/", false,
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()), false);
            return renderToString(view);
        });
    }


    public static String renderToString(Renderable r) throws IOException {
        HtmlCanvas c = new HtmlCanvas();
        r.renderOn(c);
        return c.toHtml();
    }

    private static String readFile(String path, Charset encoding)  throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
