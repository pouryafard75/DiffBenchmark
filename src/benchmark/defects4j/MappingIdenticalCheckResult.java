package benchmark.defects4j;

import java.io.Serializable;

public class MappingIdenticalCheckResult implements Serializable {
    boolean isSame_RM_GTG;
    boolean isSame_RM_GTS;
    boolean isSame_RM_ANY;

    Long rm_exe_time;
    Long gtg_exe_time;
    Long gts_exe_time;

    public MappingIdenticalCheckResult(boolean isSame_RM_GTG, boolean isSame_RM_GTS, boolean isSame_RM_ANY) {
        this.isSame_RM_GTG = isSame_RM_GTG;
        this.isSame_RM_GTS = isSame_RM_GTS;
        this.isSame_RM_ANY = isSame_RM_ANY;
    }

    public boolean isSame_RM_GTG() {
        return isSame_RM_GTG;
    }

    public void setSame_RM_GTG(boolean same_RM_GTG) {
        this.isSame_RM_GTG = same_RM_GTG;
    }

    public boolean isSame_RM_GTS() {
        return isSame_RM_GTS;
    }

    public void setSame_RM_GTS(boolean same_RM_GTS) {
        this.isSame_RM_GTS = same_RM_GTS;
    }

    public boolean isSame_RM_ANY() {
        return isSame_RM_ANY;
    }

    public void setSame_RM_ANY(boolean same_RM_ANY) {
        this.isSame_RM_ANY = same_RM_ANY;
    }

    public Long getRm_exe_time() {
        return rm_exe_time;
    }

    public void setRm_exe_time(Long rm_exe_time) {
        this.rm_exe_time = rm_exe_time;
    }

    public Long getGtg_exe_time() {
        return gtg_exe_time;
    }

    public void setGtg_exe_time(Long gtg_exe_time) {
        this.gtg_exe_time = gtg_exe_time;
    }

    public Long getGts_exe_time() {
        return gts_exe_time;
    }

    public void setGts_exe_time(Long gts_exe_time) {
        this.gts_exe_time = gts_exe_time;
    }
}
