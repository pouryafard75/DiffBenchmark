//package dat;
//
//import benchmark.metrics.computers.vanilla.BenchmarkComparisonInput;
//import benchmark.metrics.computers.vanilla.HRDBenchmarkComputer;
//import benchmark.metrics.models.BaseDiffComparisonResult;
//import benchmark.metrics.models.DiffStats;
//import benchmark.metrics.models.FileDiffComparisonResult;
//import benchmark.metrics.models.Stats;
//import benchmark.oracle.generators.diff.HRDGen3;
//import benchmark.oracle.generators.diff.HumanReadableDiffGenerator;
//import benchmark.oracle.generators.tools.models.ASTDiffTool;
//import benchmark.oracle.models.NecessaryMappings;
//import benchmark.utils.CaseInfo;
//import benchmark.utils.Configuration.Configuration;
//import benchmark.utils.PathResolver;
//import com.github.gumtreediff.actions.Diff;
//import fr.gumtree.autotuning.entity.SingleDiffResult;
//import fr.gumtree.autotuning.fitness.Fitness;
//import fr.gumtree.autotuning.gumtree.ExecutionConfiguration;
//import org.refactoringminer.astDiff.models.ASTDiff;
//import org.refactoringminer.astDiff.models.ProjectASTDiff;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//import static benchmark.oracle.generators.diff.HumanReadableDiffGenerator.isPartOfJavadoc;
//import static benchmark.oracle.generators.tools.runners.APIChanger.diffToASTDiff;
//
///* Created by pourya on 2024-01-22*/
//public class BenchrmarkFitness implements Fitness {
//    public static List<Intel> intels = new ArrayList<>();
//    final CaseInfo info;
//    private final ProjectASTDiff projectASTDiff;
//    private final ASTDiff rmDiff;
//    private final Configuration configuration;
//
//    public BenchrmarkFitness(CaseInfo info, ProjectASTDiff projectASTDiff, ASTDiff rmDiff, Configuration configuration) {
//        this.info = info;
//        this.projectASTDiff = projectASTDiff;
//        this.rmDiff = rmDiff;
//        this.configuration = configuration;
//    }
//
//
//    @Override
//    public Double getFitnessValue(SingleDiffResult singleDiffResult, ExecutionConfiguration.METRIC metric) {
//
//        ASTDiff generated = diffToASTDiff(singleDiffResult.getDiff(),
//                rmDiff.getSrcPath(),
//                rmDiff.getDstPath()
//        );
//        HumanReadableDiffGenerator datGen = new HRDGen3(
//                projectASTDiff,
//                generated,
//                info,
//                configuration
//        );
//        BenchmarkComparisonInput input = null;
//        try {
//            input = BenchmarkComparisonInput.read(configuration, info, PathResolver.fileNameAsFolder(rmDiff.getSrcPath()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        input.add(ASTDiffTool.DAT, datGen.getResult());
//        input.filterForDat();
//        HRDBenchmarkComputer hrdBenchmarkComputer = new HRDBenchmarkComputer(input);
//        BaseDiffComparisonResult fileDiffComparisonResult = new FileDiffComparisonResult(info, rmDiff.getSrcPath());
//        try {
//            hrdBenchmarkComputer.compute(fileDiffComparisonResult);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        DiffStats dat = fileDiffComparisonResult.getDiffStatsList().get(ASTDiffTool.DAT.name());
//        NecessaryMappings ignore = fileDiffComparisonResult.getIgnore().getIntraFileMappings();
//        Stats stats = dat.getAbstractMappingStats();
//        Stats finalStats = new Stats(stats.getTP() - ignore.getMappings().size(), stats.getFP(), stats.getFN());
//
//        int edSize = singleDiffResult.getDiff().editScript.asList().size();
//        int edSizeNonJavaDocs = singleDiffResult.getDiff().editScript.asList().stream().filter(x ->
//                !isPartOfJavadoc(x.getNode())).toList().size();
//
//        Intel intel = new Intel(info.getRepo(), info.getCommit(), rmDiff.getSrcPath(),
//                singleDiffResult.get("MATCHER").toString(), singleDiffResult.get("CONFIG").toString(),
//                edSize, edSizeNonJavaDocs,
//                ignore, dat);
//        intels.add(intel);
//        if (Double.isNaN(finalStats.calcF1()))
//            return 0.0;
//        return finalStats.calcF1();
//    }
//
//    private DiffStats makeStats(ASTDiff generated) {
//        HumanReadableDiffGenerator datGen = new HRDGen3(
//                projectASTDiff,
//                generated,
//                info,
//                configuration
//        );
//        BenchmarkComparisonInput input;
//        try {
//            input = BenchmarkComparisonInput.read(configuration, info, PathResolver.fileNameAsFolder(rmDiff.getSrcPath()));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        input.add(ASTDiffTool.DAT, datGen.getResult());
//        input.filterForDat();
//        HRDBenchmarkComputer hrdBenchmarkComputer = new HRDBenchmarkComputer(input);
//        BaseDiffComparisonResult fileDiffComparisonResult = new FileDiffComparisonResult(info, rmDiff.getSrcPath());
//        try {
//            hrdBenchmarkComputer.compute(fileDiffComparisonResult);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        return fileDiffComparisonResult.getDiffStatsList().get(ASTDiffTool.DAT.name());
//    }
//
//    @Override
//    public Double computeFitness(List<Double> list, ExecutionConfiguration.METRIC metric) {
//        throw new RuntimeException("Not implemented 3");
//    }
//
//
//
//
//    @Override
//    public Double getFitnessValue(Diff diff, ExecutionConfiguration.METRIC metric) {
//        throw new RuntimeException("Not implemented 1");
//    }
//}
