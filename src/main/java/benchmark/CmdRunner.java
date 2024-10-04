package benchmark;

import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;

import java.util.*;

import static benchmark.Runner.run;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class CmdRunner {
    //
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("No arguments provided");
        }
        else {
            Set<IExperiment> experiments;
            boolean rm_only = false;
            if (args[0].equals("rm")) {
                experiments = argsToConfs(Arrays.copyOfRange(args, 1, args.length));
                rm_only = true;
            }
            else  experiments = argsToConfs(args);
            run(experiments, rm_only, benchmarkHumanReadableDiffGenerator -> {
                try {
                    benchmarkHumanReadableDiffGenerator.generateMultiThreaded(Runtime.getRuntime().availableProcessors());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static Set<IExperiment> argsToConfs(String[] args) {
        Set<IExperiment> confs = new LinkedHashSet<>();
        for (String arg : args) {
            IExperiment experiment = ExperimentsEnum.findExperimentByName(arg);
            if (experiment != null) confs.add(experiment);
            else System.out.println("Configuration not found: " + arg);
        }
        return confs;
    }
}
