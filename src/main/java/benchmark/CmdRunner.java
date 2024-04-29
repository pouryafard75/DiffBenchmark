package benchmark;

import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

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
            Set<Configuration> confs;
            boolean rm_only = false;
            if (args[0].equals("rm")) {
                confs = argsToConfs(Arrays.copyOfRange(args, 1, args.length));
                rm_only = true;
            }
            else  confs = argsToConfs(args);
            run(confs, rm_only, benchmarkHumanReadableDiffGenerator -> {
                try {
                    benchmarkHumanReadableDiffGenerator.generateMultiThreaded(Runtime.getRuntime().availableProcessors());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    private static Set<Configuration> argsToConfs(String[] args) {
        Set<Configuration> confs = new LinkedHashSet<>();
        for (String arg : args) {
            Configuration confByName = ConfigurationFactory.getConfByName(arg);
            if (confByName != null) confs.add(confByName);
            else System.out.println("Configuration not found: " + arg);
        }
        return confs;
    }
}
