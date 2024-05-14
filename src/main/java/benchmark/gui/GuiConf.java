package benchmark.gui;

import benchmark.generators.tools.ASTDiffTool;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2024-04-29*/
public class GuiConf {
    public static Set<ASTDiffTool> enabled_tools = new LinkedHashSet<>();
    public static Set<DiffViewers> enabled_viewers = new LinkedHashSet<>();
    static {
        enabled_tools.addAll(Arrays.asList(ASTDiffTool.values()));
        enabled_tools.remove(ASTDiffTool.DAT);

    }

    static {
//        enabled_viewers.add(DiffViewers.MONACO);
        enabled_viewers.add(DiffViewers.VANILLA);
    }

}
