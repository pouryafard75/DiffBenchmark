package benchmark.gui.web;

import gui.webdiff.DirComparator;
import gui.webdiff.WebDiff;
import org.rendersnake.DocType;
import org.rendersnake.HtmlCanvas;
import org.rendersnake.Renderable;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import static org.rendersnake.HtmlAttributesFactory.*;

public class BenchmarkDirectoryDiffView implements Renderable {
    private final DirComparator comperator;
    private final boolean perfectExists;

    public BenchmarkDirectoryDiffView(DirComparator comperator, boolean perfectExists) {
        this.comperator = comperator;
        this.perfectExists = perfectExists;
    }

    @Override
    public void renderOn(HtmlCanvas html) throws IOException {
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
                                .render_if(new ModifiedFiles(comperator.getModifiedFilesName(),perfectExists), comperator.getModifiedFilesName().size() > 0)
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
                                        comperator.getRemovedFilesName().size() > 0)
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
                                        comperator.getAddedFilesName().size() > 0)
                            ._div()
                        ._div()
                    ._div()
                ._div()
            ._body()
        ._html();
    }

    private static class ModifiedFiles implements Renderable {
//        private List<Pair<File, File>> files;

        private Map<String,String> diffInfos;
        private final boolean perfectExistence;

        private ModifiedFiles(Map<String,String> diffInfos, boolean perfectExists) {
            this.diffInfos = diffInfos;
            perfectExistence = perfectExists;
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
                tbody
                .tr()
//                    .td().content(comparator.getSrc().toAbsolutePath().relativize(file.first.toPath().toAbsolutePath()).toString())
                    .td().content(properText(nameBefore,nameAfter))
                    .td()
                        .div(class_("btn-toolbar justify-content-end"))
                            .div(class_("btn-group"))
                                //TODO: integrate this with the -g option
//                                .if_(TreeGenerators.getInstance().hasGeneratorForFile(file.first.getAbsolutePath()))

                                    .if_(perfectExistence)
                                    .a(class_("btn btn-primary btn-sm").href("/GOD/" + id)).content("Perfect")
                                    .a(class_("btn btn-primary btn-sm").href("/GOD-monaco/" + id)).content("Perfect-monaco")
                                    ._if()
                                    .a(class_("btn btn-primary btn-sm").href("/RMD/" + id)).content("RMDiff")
                                    .a(class_("btn btn-primary btn-sm").href("/RMD-monaco/" + id)).content("RMD-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/GTG/" + id)).content("GTGreedy")
                                    .a(class_("btn btn-primary btn-sm").href("/GTG-monaco/" + id)).content("GTGreedy-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/GTS/" + id)).content("GTSimple")
                                    .a(class_("btn btn-primary btn-sm").href("/GTS-monaco/" + id)).content("GTSimple-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/IJM/" + id)).content("IJM")
                                    .a(class_("btn btn-primary btn-sm").href("/IJM-monaco/" + id)).content("IJM-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/MTD/" + id)).content("MTD")
                                    .a(class_("btn btn-primary btn-sm").href("/MTD-monaco/" + id)).content("MTD-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/GT2/" + id)).content("GT2")
                                    .a(class_("btn btn-primary btn-sm").href("/GT2-monaco/" + id)).content("GT2-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/iAST/" + id)).content("iAST")
                                    .a(class_("btn btn-primary btn-sm").href("/iAST-monaco/" + id)).content("iAST-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/RM2/" + id)).content("RM2")
                                    .a(class_("btn btn-primary btn-sm").href("/RM2-monaco/" + id)).content("RM2-monaco")
                                    .a(class_("btn btn-primary btn-sm").href("/TRV/" + id)).content("TRV")
                                    .a(class_("btn btn-primary btn-sm").href("/TRV-monaco/" + id)).content("TRV-monaco")

//
                            ._div()
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
