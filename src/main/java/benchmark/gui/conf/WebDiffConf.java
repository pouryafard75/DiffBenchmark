package benchmark.gui.conf;

import benchmark.data.exp.ToolSets;
import benchmark.generators.tools.models.IASTDiffTool;
import benchmark.gui.viewers.DiffViewers;

import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2024-04-29*/
public class WebDiffConf {
    public Set<IASTDiffTool> enabled_tools = new LinkedHashSet<>();
    public Set<DiffViewers> enabled_viewers = new LinkedHashSet<>();

    public Set<DiffViewers> getEnabled_viewers() {
        return enabled_viewers;
    }

    public Set<IASTDiffTool> getEnabled_tools() {
        return enabled_tools;
    }

    public void setEnabled_tools(Set<IASTDiffTool> enabled_tools) {
        this.enabled_tools = enabled_tools;
    }

    public void setEnabled_viewers(Set<DiffViewers> enabled_viewers) {
        this.enabled_viewers = enabled_viewers;
    }

    public void addTool(IASTDiffTool tool) {
        enabled_tools.add(tool);
    }
    public void addViewer(DiffViewers viewer) {
        enabled_viewers.add(viewer);
    }


    public static WebDiffConf defaultConf() {
        WebDiffConf conf = new WebDiffConf();
        // Tools
        conf.setEnabled_tools(ToolSets.BIG_GUNS);
        // Viewers
        conf.enabled_viewers.add(DiffViewers.MONACO);
        conf.enabled_viewers.add(DiffViewers.VANILLA);
        return conf;
    }
}
