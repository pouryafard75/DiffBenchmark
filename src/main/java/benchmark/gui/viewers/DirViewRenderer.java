package benchmark.gui.viewers;

import benchmark.generators.tools.ASTDiffTool;
import org.rendersnake.HtmlCanvas;

public interface DirViewRenderer {
    HtmlCanvas render(HtmlCanvas div, ASTDiffTool tool, int id) throws Exception;
}
