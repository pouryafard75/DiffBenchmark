package benchmark;

import benchmark.metrics.computers.vanilla.VanillaBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.writers.MetricsCsvWriter;
import benchmark.oracle.generators.BenchmarkHumanReadableDiffGenerator;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.eclipse.jgit.annotations.Nullable;

import java.util.*;

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
            for (Configuration configuration : confs) {
                if (rm_only) configuration.setActiveTools(Set.of(ASTDiffTool.GOD, ASTDiffTool.TRV, ASTDiffTool.RMD, ASTDiffTool.RM2));
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
