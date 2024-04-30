package benchmark.gui;

import benchmark.oracle.generators.tools.models.ASTDiffTool;

import java.util.EnumMap;
import java.util.Map;

/* Created by pourya on 2024-04-29*/
public class GuiConf {
    public static Map<ASTDiffTool, Boolean> enabled = new EnumMap<>(ASTDiffTool.class);
    static {
//        enabled.put(ASTDiffTool.GOD, true);
//        enabled.put(ASTDiffTool.TRV, true);
        enabled.put(ASTDiffTool.RMD, true);
        enabled.put(ASTDiffTool.GTG, true);
        enabled.put(ASTDiffTool.GTS, true);
//        enabled.put(ASTDiffTool.OBV, true);
    }

}
