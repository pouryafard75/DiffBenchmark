package benchmark.gui;

import benchmark.generators.tools.ASTDiffTool;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2024-04-29*/
public class GuiConf {
    public Set<ASTDiffTool> enabled_tools = new LinkedHashSet<>();
    public Set<DiffViewers> enabled_viewers = new LinkedHashSet<>();

    public static GuiConf defaultConf() {
        GuiConf conf = new GuiConf();
        // Tools
        conf.enabled_tools.add(ASTDiffTool.GOD);
        conf.enabled_tools.add(ASTDiffTool.RMD);
        conf.enabled_tools.add(ASTDiffTool.GTG);
        conf.enabled_tools.add(ASTDiffTool.GTS);
        conf.enabled_tools.add(ASTDiffTool.TRV);
        // Viewers
        conf.enabled_viewers.add(DiffViewers.MONACO);
        conf.enabled_viewers.add(DiffViewers.VANILLA);
        return conf;
    }
}
