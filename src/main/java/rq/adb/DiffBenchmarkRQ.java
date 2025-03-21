package rq.adb;

import benchmark.data.exp.IExperiment;
import benchmark.metrics.models.BaseDiffComparisonResult;
import rq.RQ;

import java.util.Collection;
import java.util.Map;

public interface DiffBenchmarkRQ extends RQ {
    default void run(IExperiment[] confs) throws Exception {
        calculate(confs);
    }
    Map<IExperiment, Collection<? extends BaseDiffComparisonResult>> calculate(IExperiment[] confs) throws Exception;

    static String convertToLatexTable(Collection<? extends BaseDiffComparisonResult> result) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\\begin{table}[H]\n");
        return stringBuilder.toString();
    }
}
