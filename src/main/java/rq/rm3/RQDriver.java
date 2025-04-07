package rq.rm3;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import benchmark.metrics.computers.filters.FilterDuringMetricsCalculation;
import rq.RQ;

/* Created by pourya on 2024-05-17*/
public class RQDriver {
    public static void main(String[] args) {
        IExperiment[] confs = {ExperimentsEnum.D4J_EXP_2_1};
        RQ[] rqs = {
                new RQ1(),
//                new RQ2(), //Execution time is too long, so execute it separately withing RQ2.main method.
                new RQ3(FilterDuringMetricsCalculation.PROGRAM_ELEMENTS),
                new RQ3(FilterDuringMetricsCalculation.FIELD_DECL_ONLY),
                new RQ3(FilterDuringMetricsCalculation.METHOD_DECL_ONLY),
                new RQ3(FilterDuringMetricsCalculation.TYPE_DECL_ONLY),
                new RQ3(FilterDuringMetricsCalculation.ENUM_DECL_ONLY),
                new RQ4(),
                new RQ5(),
                new RQ6(),
                new RQ7(),
                /*new RQ8(), */  /*Skip the execution time RQ in the driver.*/
        };
        for (RQ rq : rqs) {
            try {
                rq.run(confs);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
