package benchmark.gui.web;

import benchmark.oracle.generators.tools.ASTDiffTool;
import com.github.gumtreediff.utils.Pair;
import gui.webdiff.DirComparator;
import gui.webdiff.MonacoDiffView;
import gui.webdiff.VanillaDiffView;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
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
    private final Map<ASTDiffTool, Set<ASTDiff>> diffs;


    public BenchmarkWebDiff(ProjectASTDiff projectASTDiffByRM, Map<ASTDiffTool, Set<ASTDiff>> diffs){
        this.projectASTDiff = projectASTDiffByRM;
        this.diffs = diffs;
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

        DirComparator comperator = new DirComparator(projectASTDiff);
        configureSpark(comperator, this.port);
        Spark.awaitInitialization();
        System.out.println(String.format("Starting server: %s:%d.", "http://127.0.0.1", this.port));
    }

    public void configureSpark(final DirComparator comperator, int port) {
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
            Renderable view = new BenchmarkDirectoryDiffView(comperator, diffs);
            return render(view);
        });

        for (Map.Entry<ASTDiffTool, Set<ASTDiff>> astDiffToolSetEntry : diffs.entrySet()) {
            ASTDiffTool tool = astDiffToolSetEntry.getKey();
            Set<ASTDiff> astDiffs = astDiffToolSetEntry.getValue();
            get("/" + tool + "/:id" , (request, response) -> {
                int id = Integer.parseInt(request.params(":id"));
                ASTDiff astDiff = astDiffs.stream().toList().get(id);
                Renderable view = new VanillaDiffView(tool.toString(), astDiff.getSrcPath(), astDiff.getDstPath(),
                        projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                        projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                        astDiff, false);
                return render(view);
            });
            get("/" + tool + "-monaco/:id" , (request, response) -> {
                int id = Integer.parseInt(request.params(":id"));
                ASTDiff astDiff = astDiffs.stream().toList().get(id);
                Renderable view = new MonacoDiffView(tool.toString(), astDiff.getSrcPath(), astDiff.getDstPath(),
                        projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                        projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                        astDiff, id, false);
                return render(view);
            });
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



    private static String render(Renderable r) throws IOException {
        HtmlCanvas c = new HtmlCanvas();
        r.renderOn(c);
        return c.toHtml();
    }

    private static String readFile(String path, Charset encoding)  throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
