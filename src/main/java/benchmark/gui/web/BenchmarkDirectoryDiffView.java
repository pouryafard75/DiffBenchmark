package benchmark.gui.web;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.gui.viewers.DiffViewers;
import benchmark.gui.conf.GuiConf;

import gui.webdiff.WebDiff;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.rendersnake.DocType;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.lang.String;

import static org.rendersnake.HtmlAttributesFactory.*;

public class BenchmarkDirectoryDiffView implements Renderable {
    private final BenchmarkDirComparator comperator;
    private final Map<ASTDiffTool, Set<ASTDiff>> diffs;
    private final GuiConf guiConf;

    public BenchmarkDirectoryDiffView(BenchmarkDirComparator comperator, Map<ASTDiffTool, Set<ASTDiff>> diffs, GuiConf guiConf) {
        this.comperator = comperator;
        this.diffs = diffs;
        this.guiConf = guiConf;
    }

    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
        Map<String, String> modifiedFilesName = comperator.getModifiedFilesName();
        html
        .render(DocType.HTML5)
        .html(lang("en"))
            .render(new Header())
            .body()
                .div(class_("container-fluid"))
                    .div(class_("row"))
                        .render(new MenuBar())
                    ._div()
                    .div(class_("row mt-3 mb-3"))
                        .div(class_("col"))
                            .div(class_("card"))
                                .div(class_("card-header"))
                                    .h4(class_("card-title mb-0"))
                                        .write("Modified files ")
                                        .span(class_("badge badge-secondary").style("color:black")).content(comperator.getModifiedFilesName().size())
                                    ._h4()
                                ._div()
                                .render_if(new ModifiedFiles(modifiedFilesName, diffs, guiConf), !modifiedFilesName.isEmpty())
                            ._div()
                        ._div()
                    ._div()
                    .div(class_("row mb-3"))
                        .div(class_("col"))
                            .div(class_("card"))
                                .div(class_("card-header bg-danger"))
                                    .h4(class_("card-title mb-0"))
                                        .write("Deleted files ")
                                        .span(class_("badge badge-secondary").style("color:black")).content(comperator.getRemovedFilesName().size())
                                    ._h4()
                                ._div()
                                .render_if(new AddedOrDeletedFiles(comperator.getRemovedFilesName()),
                                        !comperator.getRemovedFilesName().isEmpty())
                            ._div()
                        ._div()
                        .div(class_("col"))
                            .div(class_("card"))
                                .div(class_("card-header bg-success"))
                                    .h4(class_("card-title mb-0"))
                                        .write("Added files ")
                                        .span(class_("badge badge-secondary").style("color:black")).content(comperator.getAddedFilesName().size())
                                    ._h4()
                                ._div()
                                .render_if(new AddedOrDeletedFiles(comperator.getAddedFilesName()),
                                        !comperator.getAddedFilesName().isEmpty())
                            ._div()
                        ._div()
                    ._div()
                ._div()
            ._body()
        ._html();
    }

    private static class ModifiedFiles implements Renderable {
//        private List<Pair<File, File>> files;

        private final Map<String,String> diffInfos;
        private final Map<ASTDiffTool, Set<ASTDiff>> diffs;
        private final GuiConf conf;


        private ModifiedFiles(Map<String,String> diffInfos, Map<ASTDiffTool, Set<ASTDiff>> diffs, GuiConf guiConf) {
            this.diffInfos = diffInfos;
            this.diffs = diffs;
            conf = guiConf;
        }

        @Override
        public void renderOn(HtmlCanvas html) throws IOException {
            HtmlCanvas tbody = html
            .table(class_("table card-table table-striped table-condensed mb-0"))
                .tbody();

            int id = 0;
            for (Map.Entry<String,String> entry : diffInfos.entrySet())
            {
                String nameBefore = entry.getKey();
                String nameAfter = entry.getValue();
                HtmlCanvas div = tbody
                        .tr()
//                    .td().content(comparator.getSrc().toAbsolutePath().relativize(file.first.toPath().toAbsolutePath()).toString())
                        .td().content(properText(nameBefore, nameAfter))
                        .td()
                        .div(class_("btn-toolbar justify-content-end"))
                        .div(class_("btn-group"));
                            for (ASTDiffTool tool : diffs.keySet()) {
                                if (diffs.get(tool).isEmpty()) continue;
                                for (DiffViewers enabledViewer : conf.enabled_viewers) {
                                    try {
                                        div = enabledViewer.render(div, tool, id);
                                    } catch (Exception e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            }
                            div._div()
                        ._div()
                    ._td()
                ._tr();
                id++;
            }
            tbody
                ._tbody()
                ._table();
        }

        private String properText(String nameBefore, String nameAfter) {
            if (nameBefore.equals(nameAfter))
                return nameAfter;
            else
                return nameBefore + " -> " + nameAfter;
        }
    }

    private static class AddedOrDeletedFiles implements Renderable {
        private Set<String> files;

        private AddedOrDeletedFiles(Set<String> files) {
            this.files = files;
        }

        @Override
        public void renderOn(HtmlCanvas html) throws IOException {
            HtmlCanvas tbody = html
            .table(class_("table card-table table-striped table-condensed mb-0"))
                .tbody();
            for (String filename : files) {
                tbody
                    .tr()
                        .td().content(filename)
                    ._tr();
            }
            tbody
                ._tbody()
            ._table();
        }
    }

    private static class Header implements Renderable {
        @Override
        public void renderOn(HtmlCanvas html) throws IOException {
             html
                     .head()
                        .meta(charset("utf8"))
                        .meta(name("viewport").content("width=device-width, initial-scale=1.0"))
                        .title().content("RefactoringMiner")
                        .macros().stylesheet(WebDiff.BOOTSTRAP_CSS_URL)
                        .macros().javascript(WebDiff.JQUERY_JS_URL)
                        .macros().javascript(WebDiff.BOOTSTRAP_JS_URL)
                        .macros().javascript("/dist/shortcuts.js")
                     ._head();
        }
    }

    private static class MenuBar implements Renderable {
        @Override
        public void renderOn(HtmlCanvas html) throws IOException {
            html
            .div(class_("col"))
                .div(class_("btn-toolbar justify-content-end"))
                    .div(class_("btn-group"))
                        .a(class_("btn btn-default btn-sm btn-danger").href("/quit")).content("Quit")
                    ._div()
                ._div()
            ._div();
        }
    }
}


