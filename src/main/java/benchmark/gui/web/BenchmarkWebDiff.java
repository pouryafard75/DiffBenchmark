package benchmark.gui.web;

import com.github.gumtreediff.actions.Diff;
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
import java.util.Iterator;
import java.util.Set;

import static spark.Spark.*;

;

public class BenchmarkWebDiff {
    public static final String JQUERY_JS_URL = "https://code.jquery.com/jquery-3.4.1.min.js";
    public static final String BOOTSTRAP_CSS_URL = "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/css/bootstrap.min.css";
    public static final String BOOTSTRAP_JS_URL = "https://cdn.jsdelivr.net/npm/bootstrap@5.1.3/dist/js/bootstrap.bundle.min.js";
    public static final int port = 6868;

    private final ProjectASTDiff projectASTDiff;
    public Set<ASTDiff> rmDiff;
    public Set<Diff> gtgDiff;
    public Set<Diff> gtsDiff;
    public Set<Diff> ijmDiff;
    public Set<Diff> mtdiff;
    public Set<Diff> gt2diff;
    private final Set<ASTDiff> godDiff;


    public BenchmarkWebDiff(ProjectASTDiff projectASTDiffByRM, Set<ASTDiff> rm, Set<Diff> gtg, Set<Diff> gts, Set<Diff> ijm, Set<Diff> mtd, Set<Diff> gt2, Set<ASTDiff> GOD_astDiff) {
        this.projectASTDiff = projectASTDiffByRM;
        this.rmDiff = rm;
        this.gtgDiff = gtg;
        this.gtsDiff = gts;
        this.ijmDiff = ijm;
        this.mtdiff = mtd;
        this.gt2diff = gt2;
        this.godDiff = GOD_astDiff;
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
            Renderable view = new BenchmarkDirectoryDiffView(comperator,godDiff.size() > 0);
            return render(view);
        });
        get("/RMD/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new VanillaDiffView("RefactoringMiner", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    astDiff, false);
            return render(view);
        });
        get("/RMD-monaco/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new MonacoDiffView("RefactoringMiner", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    astDiff, id,false);
            return render(view);
        });
        get("/GTG/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = gtgDiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new VanillaDiffView("GumTree-Greedy", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, false);
            return render(view);
        });
        get("/GTG-monaco/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = gtgDiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new MonacoDiffView("GumTree-Greedy", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, id,false);
            return render(view);
        });
        get("/GTS/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = gtsDiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new VanillaDiffView("GumTree-Simple", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, false);
            return render(view);
        });

        get("/GTS-monaco/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = gtsDiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new MonacoDiffView("GumTree-Simple", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, id,false);
            return render(view);
        });
        get("/IJM/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = ijmDiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new VanillaDiffView("IJM", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, false);
            return render(view);
        });

        get("/IJM-monaco/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = ijmDiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new MonacoDiffView("IJM", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, id,false);
            return render(view);
        });

        get("/MTD/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = mtdiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new VanillaDiffView("MtDiff", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, false);
            return render(view);
        });

        get("/MTD-monaco/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = mtdiff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new MonacoDiffView("MTDiff", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, id,false);
            return render(view);
        });

        get("/GT2/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = gt2diff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new VanillaDiffView("GT2", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, false);
            return render(view);
        });

        get("/GT2-monaco/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<Diff> iterator = gt2diff.iterator();
            Diff diff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                diff = iterator.next();
            }
            ASTDiff astDiff = comperator.getASTDiff(id);
            Renderable view = new MonacoDiffView("GT2", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    diff, id,false);
            return render(view);
        });

        get("/GOD/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<ASTDiff> iterator = godDiff.iterator();
            ASTDiff astDiff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                astDiff = iterator.next();
            }
            Renderable view = new VanillaDiffView("PerfectDiff", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    astDiff, false);
            return render(view);
        });
        get("/GOD-monaco/:id", (request, response) -> {
            int id = Integer.parseInt(request.params(":id"));
            int i = id;
            Iterator<ASTDiff> iterator = godDiff.iterator();
            ASTDiff astDiff = iterator.next();
            while (i > 0)
            {
                i = i-1;
                astDiff = iterator.next();
            }
            Renderable view = new MonacoDiffView("PerfectDiff", astDiff.getSrcPath(),astDiff.getDstPath(),
                    projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                    projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()),
                    astDiff, id,false);
            return render(view);
        });




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
