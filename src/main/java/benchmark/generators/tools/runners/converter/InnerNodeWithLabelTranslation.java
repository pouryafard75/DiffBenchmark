package benchmark.generators.tools.runners.converter;

import com.github.gumtreediff.matchers.Mapping;
import com.github.gumtreediff.tree.Tree;
import org.refactoringminer.astDiff.models.ExtendedMultiMappingStore;

import java.util.Map;

/* Created by pourya on 2024-11-29*/
public class InnerNodeWithLabelTranslation implements TranslationRule {
    @Override
    public void accept(Mapping foreign, Mapping local, ExtendedMultiMappingStore ms) {
        if (innerNodeWithLabel(foreign)) {
            Tree t1 = translateInnerNodeWithLabel(foreign.first.getLabel(), local.first);
            Tree t2 = translateInnerNodeWithLabel(foreign.second.getLabel(), local.second);
            if (t1 != null && t2 != null) {
                add(foreign, local, ms, new Mapping(t1, t2));
                //Match as long as their parents are identical
                while(true){
                    Tree parent1 = t1.getParent();
                    Tree parent2 = t2.getParent();
                    if (parent1 == null || parent2 == null) break;
                    if (parent1.equals(local.first) || parent2.equals(local.second)) break;
                    if (parent1.isIsomorphicTo(parent2)){
                        add(foreign, local, ms, new Mapping(parent1, parent2));
                        t1 = parent1;
                        t2 = parent2;
                    }
                    else
                    {
                        if (parent1.getType().name.equals(parent2.getType().name))
                            add(foreign, local, ms, new Mapping(parent1, parent2));
                        break;
                    }
                }
            }
        }
    }

    private static boolean innerNodeWithLabel(Mapping mapping) {
        return nonLeafWithLabel(mapping.first) || nonLeafWithLabel(mapping.second);
    }

    private static boolean nonLeafWithLabel(Tree entry) {
        return !entry.isLeaf() && !entry.getLabel().isEmpty();
    }

    private Tree translateInnerNodeWithLabel(String label, Tree eqv) {
        if (eqv == null) return null;
        if (label.isEmpty()) return null;
        for (Tree candidate : eqv.preOrder()) {
            if (candidate.getLabel().equals(label)) {
                return candidate;
            }
        }
        String corresponding = checkTable(label);
        if (corresponding != null && !corresponding.equals(label))
            return translateInnerNodeWithLabel(corresponding, eqv);
//        System.out.println("RECORD LOG Label not found: " + label);
        return null;
    }

    private static String checkTable(String label) {
        return correspondenceTable.get(label);
    }
    public static final Map<String, String> correspondenceTable = Map.ofEntries(
            Map.entry("PLUS", "+"),
            Map.entry("MINUS", "-"),
            Map.entry("MUL", "*"),
            Map.entry("DIV", "/"),
            Map.entry("MOD", "%"),
            Map.entry("NOT", "!"),
            Map.entry("EQUAL_EQUAL", "=="),
            Map.entry("LE", "<="),
            Map.entry("LT", "<"),
            Map.entry("GE", ">="),
            Map.entry("GT", ">"),
            Map.entry("NE", "!="),
            Map.entry("COMPL", "~"),
            Map.entry("SL", "<<"),
            Map.entry("SR", ">>"),
            Map.entry("USR", ">>>"),
            Map.entry("OR", "||"),
            Map.entry("AND", "&&"),
            Map.entry("BITXOR", "^"),
            Map.entry("BITAND", "&"),
            Map.entry("BITOR", "|")
    );
}
