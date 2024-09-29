package benchmark.gui.viewers;

import benchmark.generators.tools.ASTDiffTool;
import benchmark.generators.tools.models.IASTDiffTool;
import org.rendersnake.HtmlCanvas;

public interface DirViewRenderer {
    HtmlCanvas render(HtmlCanvas div, IASTDiffTool tool, int id) throws Exception;
}
