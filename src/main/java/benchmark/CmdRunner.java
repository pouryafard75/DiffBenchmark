package benchmark;

import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.oracle.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

/* Created by pourya on 2023-04-17 7:45 p.m. */
public class CmdRunner {
    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            throw new RuntimeException("No arguments provided");
        }
        else {
            Set<Configuration> confs = argsToConfs(args);
            for (Configuration configuration : confs) {
                new BenchmarkHumanReadableDiffGenerator(configuration).generate();
                VanillaBenchmarkComputer computer = new VanillaBenchmarkComputer(configuration);
                Collection<? extends BaseDiffComparisonResult> stats = computer.compute();
                MetricsCsvWriter.exportToCSV(stats, configuration.getOutputFolder() + configuration.getName() + ".csv", true);
            }
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
