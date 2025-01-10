package benchmark.data.exp;

import benchmark.generators.tools.ASTDiffToolEnum;
import benchmark.generators.tools.models.IASTDiffTool;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2024-09-29*/
public enum ToolSets implements Set<IASTDiffTool> {
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
                        ASTDiffToolEnum.IJM,
                        ASTDiffToolEnum.MTD,
                        ASTDiffToolEnum.GT2)
    ),
    EXPERIMENT(
            Set.of(
                ASTDiffToolEnum.RMD,
                ASTDiffToolEnum.GTG,
                ASTDiffToolEnum.GTS,
                ASTDiffToolEnum.SPN_OFFSET_TRANSLATED_WITH_RULES
            )
    );

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
