package dat;

import benchmark.metrics.computers.vanilla.BenchmarkComparisonInput;
import benchmark.metrics.computers.vanilla.HRDBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.FileDiffComparisonResult;
import benchmark.oracle.generators.diff.HRDGen3;
import benchmark.oracle.generators.diff.HumanReadableDiffGenerator;
import benchmark.oracle.generators.tools.models.ASTDiffTool;
import benchmark.oracle.models.NecessaryMappings;
import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.PathResolver;
import com.github.gumtreediff.actions.ChawatheScriptGenerator;
import com.github.gumtreediff.actions.Diff;
import com.github.gumtreediff.actions.EditScript;
import com.github.gumtreediff.matchers.*;
import com.github.gumtreediff.tree.Tree;
import com.github.gumtreediff.tree.TreeContext;
import fr.gumtree.autotuning.domain.CategoricalParameterDomain;
import fr.gumtree.autotuning.domain.DoubleParameterDomain;
import fr.gumtree.autotuning.domain.IntParameterDomain;
import fr.gumtree.autotuning.domain.ParameterDomain;
import fr.gumtree.autotuning.gumtree.ParametersResolvers;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static benchmark.oracle.generators.diff.HumanReadableDiffGenerator.isPartOfJavadoc;
import static benchmark.oracle.generators.tools.runners.APIChanger.diffToASTDiff;
import static dat.BenchrmarkFitness.intels;

/* Created by pourya on 2024-02-01*/
public class GridSearch {
    private final static Logger logger = LoggerFactory.getLogger(GridSearch.class);
    private static final Matcher[] matchers = new Matcher[] {
            new CompositeMatchers.SimpleGumtree(),
            new CompositeMatchers.ClassicGumtree(),
            new CompositeMatchers.HybridGumtree(),
    };
    private static final int nrthreads = 50;
    private static final long timeout = 1000;
    private static final String PARALLEL_LEVEL = "PROPERTY_LEVEL";
    private final CaseInfo info;
    private final ProjectASTDiff projectASTDiff;
    private final Tree src;
    private final Tree dst;
    private final ASTDiff rmDiff;
    private final Configuration configuration;
    private static Map<String, List<GumtreeProperties>> cacheCombinations = new HashMap<>();


    static{
        ParametersResolvers domain = setParameters();
        initCacheCombinationProperties(domain);
    }

    public GridSearch(CaseInfo info, ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, Configuration configuration)
    {
        this.info = info;
        this.projectASTDiff = projectASTDiff;
        rmDiff = rm_astDiff;
        this.configuration = configuration;
        src = rm_astDiff.src.getRoot();
        dst = rm_astDiff.dst.getRoot();

    }

    public void run(int numThreads) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Callable<Void>> tasks = new LinkedList<>();
        logger.info("Preparing tasks");
        for (Matcher matcher : matchers) {
            List<GumtreeProperties> allCombinations = cacheCombinations.get(matcher.getClass().getCanonicalName());
            for (GumtreeProperties properties : allCombinations) {
                tasks.add(() -> {
                    addIntel(matcher, properties);
                    return null;
                });
            }
        }

        logger.info(tasks.size() + " tasks have been submitted");
        try {
            executorService.invokeAll(tasks);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executorService.shutdown();
            if (!executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS)) {
                System.err.println("ExecutorService did not terminate within the timeout.");
            }
        }
    }
    private void addIntel(Matcher matcher, GumtreeProperties properties) {
        logger.debug("Working on " + matcher + " " + properties);
        Diff diff = makeDiff(matcher, properties);
        BaseDiffComparisonResult compResult = makeStats(diffToASTDiff(diff, rmDiff.getSrcPath(), rmDiff.getDstPath()));
//        logger.debug("Stats has been generated for " + matcher + " " + properties);
        DiffStats dat = compResult.getDiffStatsList().get(ASTDiffTool.DAT.name());
        NecessaryMappings ignore = compResult.getIgnore().getIntraFileMappings();
        int edSize = diff.editScript.asList().size();
        int edSizeNonJavaDocs = diff.editScript.asList().stream().filter(x ->
                !isPartOfJavadoc(x.getNode())).toList().size();
//        logger.debug("About to add the intel for " + matcher + " " + properties);
        Intel intel = new Intel(info.getRepo(), info.getCommit(), rmDiff.getSrcPath(),
                matcher.getClass().getName(), properties.toString(),
                edSize, edSizeNonJavaDocs,
                ignore, dat);
        logger.debug("Intel #" + (intels.size() + 1) + " is added");
        intels.add(intel);
    }

    private BaseDiffComparisonResult makeStats(ASTDiff generated) {
        HumanReadableDiffGenerator datGen = new HRDGen3(
                projectASTDiff,
                generated,
                info,
                configuration
        );
        BenchmarkComparisonInput input;
        try {
            input = BenchmarkComparisonInput.read(configuration, info, PathResolver.fileNameAsFolder(rmDiff.getSrcPath()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        input.add(ASTDiffTool.DAT, datGen.getResult());
        input.filterForDat();
        HRDBenchmarkComputer hrdBenchmarkComputer = new HRDBenchmarkComputer(input);
        BaseDiffComparisonResult fileDiffComparisonResult = new FileDiffComparisonResult(info, rmDiff.getSrcPath());
        try {
            hrdBenchmarkComputer.compute(fileDiffComparisonResult);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return fileDiffComparisonResult;
    }
    private Diff makeDiff(Matcher matcher, GumtreeProperties properties) {
        CompositeMatchers.CompositeMatcher cm = (CompositeMatchers.CompositeMatcher) matcher;
        cm.configure(properties);
        MappingStore mappings = matcher.match(src, dst);
        ChawatheScriptGenerator edGenerator = new ChawatheScriptGenerator();
        EditScript actions = edGenerator.computeActions(mappings);
        TreeContext leftContext = new TreeContext();
        leftContext.setRoot(src);
        TreeContext rightContext = new TreeContext();
        rightContext.setRoot(dst);
//        logger.debug("Diff has been created for " + matcher + " " + properties);
        return new Diff(leftContext, rightContext, mappings, actions);
    }

    private static ParametersResolvers setParameters() {
        ParametersResolvers domain = new ParametersResolvers();
        domain.getParametersDomain().put(ConfigurationOptions.bu_minsize, new IntParameterDomain(ConfigurationOptions.bu_minsize.name(), Integer.class, 1000, 100, 2000, 100));
        domain.getParametersDomain().put(ConfigurationOptions.bu_minsim, new DoubleParameterDomain(ConfigurationOptions.bu_minsim.name(), Double.class, 0.5, 0.1, 1.0, 0.1));
        domain.getParametersDomain().put(ConfigurationOptions.st_minprio, new IntParameterDomain(ConfigurationOptions.st_minprio.name(), Integer.class, 2, 1, 5, 1));
        domain.getParametersDomain().put(ConfigurationOptions.st_priocalc, new CategoricalParameterDomain(ConfigurationOptions.st_priocalc.name(), String.class, "height", new String[] { "size", "height" }));
        return domain;
    }
    public static void initCacheCombinationProperties(ParametersResolvers domain) {
        cacheCombinations.clear();
        for (Matcher matcher : matchers) {
            List<GumtreeProperties> allCombinations = computesCombinations(matcher, domain);
            cacheCombinations.put(matcher.getClass().getCanonicalName(), allCombinations);
            logger.info(matcher.getClass().getCanonicalName() + " has " + allCombinations.size() + " combinations");
        }
        logger.info("All combinations have been computed");
    }
    public static List<GumtreeProperties> computesCombinations(Matcher matcher, ParametersResolvers domain) {
        List<GumtreeProperties> combinations;
        ConfigurableMatcher configurableMatcher = (ConfigurableMatcher) matcher;

        // We collect the options of the matcher
        Set<ConfigurationOptions> options = configurableMatcher.getApplicableOptions();

        List<ParameterDomain> domains = new ArrayList<>();

        // We collect the domains
        for (ConfigurationOptions option : options) {

            ParameterDomain<?> paramOption = domain.getParametersDomain().get(option);
            if (paramOption != null) {
                domains.add(paramOption);
            } else {
                logger.error("Missing config for " + option);
                throw new RuntimeException("Missing config for " + option);
            }
        }

        // Now, the CartesianProduct of all options
        combinations = computeCartesianProduct(domains);
        return combinations;
    }
    public static List<GumtreeProperties> computeCartesianProduct(List<ParameterDomain> domains) {

        return cartesianProduct(0, domains);
    }
    private static List<GumtreeProperties> cartesianProduct(int index, List<ParameterDomain> domains) {

        List<GumtreeProperties> ret = new ArrayList<>();

        if (index == domains.size()) {
            ret.add(new GumtreeProperties());
        } else {

            ParameterDomain domainOfParameters = domains.get(index);
            for (Object valueFromDomain : domainOfParameters.computeInterval()) {
                List<GumtreeProperties> configurationFromOthersDomains = cartesianProduct(index + 1, domains);
                for (GumtreeProperties configFromOthers : configurationFromOthersDomains) {

                    ConfigurationOptions value = ConfigurationOptions.valueOf(domainOfParameters.getId());
                    if (value != null) {
                        configFromOthers.put(value, valueFromDomain);
                        ret.add(configFromOthers);
                    }
                }
            }
        }
        return ret;
    }
}

