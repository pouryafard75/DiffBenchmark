package benchmark.data.exp;

import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.IASTDiffTool;

import java.util.*;

/* Created by pourya on 2024-09-29*/
public enum ToolSets implements Set<IASTDiffTool> {
    PERFECTION_BATTLE(
            Set.of(
                    ASTDiffToolEnum.RMD,
                    ASTDiffToolEnum.GOD)
    ),
    THREE_ZERO_COMPATIBLE(
            Set.of(
                    ASTDiffToolEnum.RMD,
                    ASTDiffToolEnum.GTG,
                    ASTDiffToolEnum.GTS)
    ),
    TWO_ONE_COMPATIBLE(
                Set.of(
                        ASTDiffToolEnum.RMD,
                        ASTDiffToolEnum.GTG,
                        ASTDiffToolEnum.GTS,
                        ASTDiffToolEnum.IJM_I,
                        ASTDiffToolEnum.MTD_I,
                        ASTDiffToolEnum.GT2_I)
    ),
    BIG_GUNS(
            new LinkedHashSet<>(){{
                add(ASTDiffToolEnum.RMD);
                add(ASTDiffToolEnum.GTG);
                add(ASTDiffToolEnum.GTS);
            }}
    ),
    CUSTOM_LITERATURE_TOOLS(
            new LinkedHashSet<>(BIG_GUNS){{
                add(ASTDiffToolEnum.IAM_I);
            }}
    ),
    LITERATURE_TOOLS(
            new LinkedHashSet<>(BIG_GUNS){{
                add(ASTDiffToolEnum.GT2_I);
                add(ASTDiffToolEnum.IJM_I);
                add(ASTDiffToolEnum.MTD_I);
                add(ASTDiffToolEnum.IAM_I);
            }}
    ),
    INTERFILE_EXTENSION_BATTLE_TOOLS(
            new LinkedHashSet<>(){{
//                add(ASTDiffToolEnum.GTG);
//                add(ASTDiffToolEnum.GTS);
                add(ASTDiffToolEnum.EXT_SVN_G);
                add(ASTDiffToolEnum.EXT_SVN_S);
                add(ASTDiffToolEnum.EXT_STM_G);
                add(ASTDiffToolEnum.EXT_STM_S);
            }}
    ),
    MULTI_MAPPING_SEM_BATTLE_TOOLS(
            new LinkedHashSet<>(){{
                add(ASTDiffToolEnum.EXT_NMS_G);
                add(ASTDiffToolEnum.EXT_NMS_S);
                add(ASTDiffToolEnum.EXT_FGT_G);
                add(ASTDiffToolEnum.EXT_FGT_S);
            }}
    ),
    MULTIMAPPING_EXTENSION_BATTLE_TOOLS(
            new LinkedHashSet<>(){{
                add(ASTDiffToolEnum.GTG);
                add(ASTDiffToolEnum.GTS);
                add(ASTDiffToolEnum.EXT_NMS_G);
                add(ASTDiffToolEnum.EXT_NMS_S);
            }}
    ),
    SEMANTIC_VIOLATION_EXTENSION_BATTLE_TOOLS(
            new LinkedHashSet<>(){{
                add(ASTDiffToolEnum.EXT_FGT_G);
                add(ASTDiffToolEnum.EXT_FGT_S);
//                add(ASTDiffToolEnum.FTG);
//                add(ASTDiffToolEnum.FTS);
            }}
    ),
    ARTIFICIAL(
            new LinkedHashSet<>()
            {{
                add(ASTDiffToolEnum.EXT_FGT_STM_NMS_G);
                add(ASTDiffToolEnum.EXT_FGT_SVN_NMS_G);
                add(ASTDiffToolEnum.EXT_FGT_STM_NMS_S);
                add(ASTDiffToolEnum.EXT_FGT_SVN_NMS_S);
            }}
    ),

    SPOON_AND_BIG_GUNS(
            new LinkedHashSet<>(BIG_GUNS){{
                add(ASTDiffToolEnum.SPN_T);
            }}
    ),

    VISITOR_EXP_BATTLE_TOOLS(
            new LinkedHashSet<>(){{
                add(ASTDiffToolEnum.SPN_T);
                add(ASTDiffToolEnum.SPN_G_T);
                add(ASTDiffToolEnum.SPN_S_T);
                add(ASTDiffToolEnum.GTG);
                add(ASTDiffToolEnum.GTS);
            }}
    ),
    ALL(
        new LinkedHashSet<>(){{
            add(ASTDiffToolEnum.RMD);
            add(ASTDiffToolEnum.GTG);
            add(ASTDiffToolEnum.GTS);
            add(ASTDiffToolEnum.IJM_T);
            add(ASTDiffToolEnum.MTD_T);
            add(ASTDiffToolEnum.GT2_T);
            add(ASTDiffToolEnum.IAM_T);
            add(ASTDiffToolEnum.IAM_I);
            add(ASTDiffToolEnum.SPN_T);
        }}
    ),


    BeforeAndAfterTranslations(
            new LinkedHashSet<>(){{
                add(ASTDiffToolEnum.IJM_T);
                add(ASTDiffToolEnum.IJM_I);
                add(ASTDiffToolEnum.MTD_T);
                add(ASTDiffToolEnum.MTD_I);
                add(ASTDiffToolEnum.GT2_T);
                add(ASTDiffToolEnum.GT2_I);
                add(ASTDiffToolEnum.IAM_T);
                add(ASTDiffToolEnum.IAM_I);
                add(ASTDiffToolEnum.SPN_I);
                add(ASTDiffToolEnum.SPN_T);
            }}
    ),




    ;




    private final Set<IASTDiffTool> tools;

    ToolSets(Set<IASTDiffTool> tools) {
        this.tools = new LinkedHashSet<>(tools);
    }
    ;


    @Override
    public boolean remove(Object o) {
        return tools.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return tools.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends IASTDiffTool> c) {
        return false;
    }


    @Override
    public boolean retainAll(Collection<?> c) {
        return tools.retainAll(c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return tools.removeAll(c);
    }

    @Override
    public void clear() {
        tools.clear();
    }

    @Override
    public int size() {
        return tools.size();
    }

    @Override
    public boolean isEmpty() {
        return tools.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return tools.contains(o);
    }

    @Override
    public Iterator<IASTDiffTool> iterator() {
        return tools.iterator();
    }


    @Override
    public Object[] toArray() {
        return tools.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return tools.toArray(a);
    }

    @Override
    public boolean add(IASTDiffTool iastDiffTool) {
        return false;
    }

}
