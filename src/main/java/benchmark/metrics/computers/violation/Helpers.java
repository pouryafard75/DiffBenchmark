package benchmark.metrics.computers.violation;

import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.models.ASTDiff;

import java.util.Arrays;

/* Created by pourya on 2023-12-11 3:25 p.m. */
public class Helpers {
    public static boolean contains(ASTDiff perfect, Mapping mapping) {
        for (Mapping godMapping : perfect.getAllMappings()) {
            if (isEquivalent(godMapping,mapping)) return true;
        }
        return false;
    }

    public static String makeKey(Mapping violation) {
        String p1 = violation.first.getParent().getType().name;
        String p2 = violation.second.getParent().getType().name;
        String[] types = {p1, p2};
        Arrays.sort(types);
        return  types[0] + ":" + types[1];
    }
    public static boolean isEquivalent(Mapping godMapping, Mapping mapping) {
        return
                mapping.first.getPos() == godMapping.first.getPos()
                        &&
                        mapping.first.getEndPos() == godMapping.first.getEndPos()
                        &&
                        mapping.second.getPos() == godMapping.second.getPos()
                        &&
                        mapping.second.getEndPos() == godMapping.second.getEndPos()
                        &&
                        mapping.first.getType().name.equals(godMapping.first.getType().name)
                        &&
                        mapping.second.getType().name.equals(godMapping.second.getType().name)
                        &&
                        mapping.first.getLabel().equals(godMapping.first.getLabel())
                        &&
                        mapping.second.getLabel().equals(godMapping.second.getLabel());
    }
}
