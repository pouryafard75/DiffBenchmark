package benchmark.oracle.generators.tools.adaptation;

import benchmark.oracle.generators.tools.runners.APIChanger;
import benchmark.oracle.generators.tools.runners.GT2;
import com.github.gumtreediff.matchers.Mapping;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.refactoringminer.astDiff.matchers.ExtendedMultiMappingStore;

import static benchmark.metrics.computers.violation.simplename.GT2Helpers.getEqv;
import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2023-12-20*/
public class Helper {
    public static void main(String[] args) throws Exception {
        ProjectASTDiff projectASTDiff = runWhatever("Chart", "1");
        ASTDiff rm = projectASTDiff.getDiffSet().iterator().next();
        APIChanger apiChanger = new GT2(projectASTDiff, rm);
        ASTDiff mirror = apiChanger.makeASTDiff();
        ExtendedMultiMappingStore adoptedMappings = new ExtendedMultiMappingStore(rm.src.getRoot(), rm.dst.getRoot());
        for (Mapping mirror_mapping : mirror.getAllMappings()) {
            adoptedMappings.addMapping(
                getEqv(mirror_mapping.first, rm.src.getRoot()),
                getEqv(mirror_mapping.second, rm.dst.getRoot())
            );
        }
        ASTDiff adopted = new ASTDiff(
                    rm.getSrcPath(),
                    rm.getDstPath(),
                    rm.src,
                    rm.dst,
                    adoptedMappings
                    );

//        astDiff.createRootNodesClassifier().getUpdatedDsts().forEach(System.out::println);
//        rm.createRootNodesClassifier().getUpdatedDsts().forEach(System.out::println);
    }

}

