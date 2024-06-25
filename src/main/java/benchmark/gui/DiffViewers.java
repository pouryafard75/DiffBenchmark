package benchmark.gui;

import benchmark.generators.tools.ASTDiffTool;
import gui.webdiff.MonacoDiffView;
import gui.webdiff.VanillaDiffView;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import java.util.Set;

import static benchmark.gui.web.BenchmarkWebDiff.renderToString;
import static org.rendersnake.HtmlAttributesFactory.class_;
import static spark.Spark.get;

/* Created by pourya on 2024-05-03*/
public enum DiffViewers implements DirViewRenderer, SparkConfigurator {
    VANILLA(
            (div, tool, id) -> div.a(class_("btn btn-primary btn-sm").href("/" + tool + "/" + id)).content(tool.name()),
            (tool, astDiffs, projectASTDiff) -> get("/" + tool + "/:id" , (request, response) -> {
                int id = Integer.parseInt(request.params(":id"));
                ASTDiff astDiff = astDiffs.stream().toList().get(id);
                Renderable view = new VanillaDiffView(tool.toString(), astDiff.getSrcPath(), astDiff.getDstPath(),
                        astDiff, id, projectASTDiff.getDiffSet().size(),  "", false,
                        projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                        projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()), false);
                return renderToString(view);
            })
    ),
    MONACO(
            (div, tool, id) -> div.a(class_("btn btn-primary btn-sm").href("/" + tool + "-monaco/" + id)).content(tool.name() + "-monaco"),
            (tool, astDiffs, projectASTDiff) -> get("/" + tool + "-monaco/:id" , (request, response) -> {
                int id = Integer.parseInt(request.params(":id"));
                ASTDiff astDiff = astDiffs.stream().toList().get(id);
                Renderable view = new MonacoDiffView(tool.toString(), astDiff.getSrcPath(), astDiff.getDstPath(),
                        astDiff, id, projectASTDiff.getDiffSet().size(), "", false);
                return renderToString(view);
            })
    )
    ;
    private final DirViewRenderer dirView;
    private final SparkConfigurator sparkConfigurator;
    DiffViewers(DirViewRenderer dirView, SparkConfigurator sparkConfigurator) {
        this.dirView = dirView;
        this.sparkConfigurator = sparkConfigurator;
    }
    public HtmlCanvas render(HtmlCanvas div, ASTDiffTool tool, int id) throws Exception {
        return dirView.render(div, tool, id);
    }

    public void configure(ASTDiffTool tool, Set<ASTDiff> astDiffs, ProjectASTDiff projectASTDiff) throws Exception {
        sparkConfigurator.configure(tool, astDiffs, projectASTDiff);
    }
}
interface DirViewRenderer {
    HtmlCanvas render(HtmlCanvas div, ASTDiffTool tool, int id) throws Exception;
}
interface SparkConfigurator {
    void configure(ASTDiffTool tool, Set<ASTDiff> astDiffs, ProjectASTDiff projectASTDiff) throws Exception;
}
