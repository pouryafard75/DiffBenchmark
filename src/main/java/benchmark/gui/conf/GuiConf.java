package benchmark.gui.conf;

import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.gui.viewers.DiffViewers;

import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2024-04-29*/
public class GuiConf {
    public Set<ASTDiffToolEnum> enabled_tools = new LinkedHashSet<>();
    public Set<DiffViewers> enabled_viewers = new LinkedHashSet<>();

    public static GuiConf defaultConf() {
        GuiConf conf = new GuiConf();
        // Tools
        conf.enabled_tools.add(ASTDiffToolEnum.RMD);
        conf.enabled_tools.add(ASTDiffToolEnum.GTG);
        conf.enabled_tools.add(ASTDiffToolEnum.GTS);
        conf.enabled_tools.add(ASTDiffToolEnum.GOD);
//        conf.enabled_tools.add(ASTDiffToolEnum.GGO);
//        conf.enabled_tools.add(ASTDiffToolEnum.GSO);
//        conf.enabled_tools.add(ASTDiffToolEnum.SPN);
//        conf.enabled_tools.add(ASTDiffToolEnum.SPN_PRV);
//        conf.enabled_tools.add(ASTDiffToolEnum.SPN_COMP);
        // Viewers
        conf.enabled_viewers.add(DiffViewers.MONACO);
        conf.enabled_viewers.add(DiffViewers.VANILLA);
        return conf;
    }
}
