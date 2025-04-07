package benchmark.gui.viewers;


import benchmark.generators.tools.models.IASTDiffTool;
import gui.webdiff.viewers.monaco.MonacoView;
import gui.webdiff.viewers.vanilla.VanillaDiffView;
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
            (div, tool, id) -> div.a(class_("btn btn-primary btn-sm").href("/" + tool.getShortName() + "/" + id)).content(tool.getShortName()),
            (tool, astDiffs, projectASTDiff) -> get("/" + tool.getShortName() + "/:id" , (request, response) -> {
                int id = Integer.parseInt(request.params(":id"));
                ASTDiff astDiff = astDiffs.stream().toList().get(id);
                VanillaDiffView vanillaDiffView = new VanillaDiffView(tool.getToolName(), astDiff.getSrcPath(), astDiff.getDstPath(),
                        astDiff, id, projectASTDiff.getDiffSet().size(), "", false,
                        projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                        projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()), false);
                return renderToString(vanillaDiffView);
            })
    ),
    MONACO(
            (div, tool, id) -> div.a(class_("btn btn-primary btn-sm").href("/" + tool.getShortName() + "-monaco/" + id)).content(tool.getShortName() + "-monaco"),
            (tool, astDiffs, projectASTDiff) -> get("/" + tool.getShortName() + "-monaco/:id" , (request, response) -> {
                int id = Integer.parseInt(request.params(":id"));
                ASTDiff astDiff = astDiffs.stream().toList().get(id);
                Renderable view = new MonacoView(tool.getToolName(), astDiff.getSrcPath(), astDiff.getDstPath(),
                        astDiff, id, projectASTDiff.getDiffSet().size(), "", false,
                        projectASTDiff.getFileContentsBefore().get(astDiff.getSrcPath()),
                        projectASTDiff.getFileContentsAfter().get(astDiff.getDstPath()));
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
    public HtmlCanvas render(HtmlCanvas div, IASTDiffTool tool, int id) throws Exception {
        return dirView.render(div, tool, id);
    }

    public void configure(IASTDiffTool tool, Set<ASTDiff> astDiffs, ProjectASTDiff projectASTDiff) throws Exception {
        sparkConfigurator.configure(tool, astDiffs, projectASTDiff);
    }
}

