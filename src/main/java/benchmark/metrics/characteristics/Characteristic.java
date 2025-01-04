package benchmark.metrics.characteristics;

import benchmark.data.dataset.IBenchmarkDataset;
import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.metrics.computers.churn.ChurnCalculator;

import org.apache.commons.lang3.function.TriFunction;
import org.apache.commons.lang3.tuple.Pair;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static benchmark.generators.tools.ASTDiffToolEnum.GOD;

/* Created by pourya on 2024-07-01*/
public enum Characteristic {
    NUM_OF_CASES(benchmarkDataset -> benchmarkDataset.getCases().size()),
    NUM_OF_FILES(benchmarkDataset -> eachCaseIterator(benchmarkDataset,
            (projectASTDiff, info, number) -> number.intValue() + projectASTDiff.getDiffSet().size())),
    NUM_OF_CASES_WITH_REFACTORINGS(
            benchmarkDataset -> eachCaseIterator(benchmarkDataset,
                    (projectASTDiff, info, number) ->
                            (projectASTDiff.getRefactorings().isEmpty()) ? number.intValue() : number.intValue() + 1)),
    NUM_OF_CASES_WITH_MULTI_MAPPINGS(
            benchmarkDataset -> eachCaseIterator(benchmarkDataset,
                    (projectASTDiff, benchmarkCase, number) -> {
                        boolean hasMultiMappings = false;
                        for (ASTDiff astDiff : projectASTDiff.getDiffSet()) {
                            ASTDiff diff = null;
                            try {
                                diff = GOD.diff(benchmarkCase, (x) -> astDiff);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                            if (!diff.getAllMappings().srcToDstMultis().isEmpty() || !diff.getAllMappings().dstToSrcMultis().isEmpty()) {
                                hasMultiMappings = true;
                                break;
                            }
                        }
                        return hasMultiMappings ? number.intValue() + 1 : number.intValue();
                    })),
    AVG_CHURN(Characteristic::getAvgChurn),
    INDEX_PRINTER(configuration -> eachCaseIterator(configuration, (projectASTDiff, info, number) -> {
        AtomicInteger result = new AtomicInteger(number.intValue());
        projectASTDiff.getDiffSet().forEach(astDiff -> {
            result.addAndGet(1);
            System.out.println("#" + result + " " + info.getRepo() + " " + info.getCommit() + " " + astDiff.getSrcPath());
        });
        return result;
    })),
    ;

    private static Number eachCaseIterator(IBenchmarkDataset benchmarkDataset, TriFunction<ProjectASTDiff, IBenchmarkCase, Number, Number> consumer) {
        Number number = 0;
        for (IBenchmarkCase info : benchmarkDataset.getCases()) {
            ProjectASTDiff projectASTDiff = info.getProjectASTDiff();
            number = consumer.apply(projectASTDiff, info, number);
        }
        return number;
    }

    private final Function<IBenchmarkDataset, Number> executor;

    Characteristic(Function<IBenchmarkDataset, Number> executor) {
        this.executor = executor;
    }

    public Number getNumber(IBenchmarkDataset experimentConfiguration) {
        return executor.apply(experimentConfiguration);
    }

    private static float getAvgChurn(IBenchmarkDataset benchmarkDataset) {
        float totalLeft = 0.0f;
        float totalRight = 0.0f;
        int commitCount = 0;
        for (IBenchmarkCase info : benchmarkDataset.getCases()) {
            System.out.println("Processing: " + info.getRepo() + " " + info.getCommit());
            ProjectASTDiff projectASTDiff = info.getProjectASTDiff();
            Pair<Float, Float> floatFloatPair = ChurnCalculator.calculateRelativeAddDeleteChurn
                    (projectASTDiff, false, false);
            float leftChurn = floatFloatPair.getLeft();
            float rightChurn = floatFloatPair.getRight();

            // Accumulate values for left and right floats
            totalLeft += leftChurn;
            totalRight += rightChurn;

            commitCount++;
        }
        float averageLeft = totalLeft / commitCount;
        float averageRight = totalRight / commitCount;


//        System.out.println("Average Left Churn: " + averageLeft);
//        System.out.println("Average Right Churn: " + averageRight);
        return (averageLeft + averageRight) / 2;
    }
}

