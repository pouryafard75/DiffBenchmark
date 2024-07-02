package dat;

import benchmark.metrics.computers.vanilla.BenchmarkComparisonInput;
import benchmark.metrics.computers.vanilla.HRDBenchmarkComputer;
import benchmark.metrics.models.BaseDiffComparisonResult;
import benchmark.metrics.models.DiffStats;
import benchmark.metrics.models.FileDiffComparisonResult;
import benchmark.generators.hrd.HRDGen3;
import benchmark.generators.hrd.HumanReadableDiffGenerator;
import benchmark.generators.tools.ASTDiffTool;
import benchmark.models.NecessaryMappings;
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
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.refactoringminer.astDiff.utils.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static benchmark.generators.hrd.HumanReadableDiffGenerator.isPartOf;
import static benchmark.generators.tools.runners.shaded.AbstractASTDiffProviderFromIncompatibleTree.diffToASTDiffWithActions;

/* Created by pourya on 2024-02-01*/
public class GridSearch {
    private final static Logger logger = LoggerFactory.getLogger(GridSearch.class);
    private static final List<Class<? extends Matcher>> matchers = Arrays.asList(
            CompositeMatchers.SimpleGumtree.class
            ,CompositeMatchers.ClassicGumtree.class
            ,CompositeMatchers.HybridGumtree.class
    );
    private final List<Intel> intels;
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
        try {
            initCacheCombinationProperties(domain);
        } catch (Exception e) {
            logger.error("Error in initCacheCombinationProperties", e);
            throw new RuntimeException(e);
        }
    }

    public GridSearch(CaseInfo info, ProjectASTDiff projectASTDiff, ASTDiff rm_astDiff, Configuration configuration)
    {
        this.info = info;
        this.projectASTDiff = projectASTDiff;
        rmDiff = rm_astDiff;
        this.configuration = configuration;
        src = rm_astDiff.src.getRoot();
        dst = rm_astDiff.dst.getRoot();
        intels = new ArrayList<>();

    }
    public static Matcher getMatcher(String algoName) {
        if (algoName.equals("SimpleGumtree")) {
            return new CompositeMatchers.SimpleGumtree();
        } else if (algoName.equals("ClassicGumtree")) {
            return new CompositeMatchers.ClassicGumtree();
        } else if (algoName.equals("HybridGumtree")) {
            return new CompositeMatchers.HybridGumtree();
        } else {
            throw new RuntimeException("Unknown algorithm: " + algoName);
        }
    }
    public List<Intel> run(int numThreads) throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        List<Callable<Void>> tasks = new LinkedList<>();
        logger.info("Preparing tasks");
        for (Class<? extends Matcher> matcher : matchers) {
            List<GumtreeProperties> allCombinations = cacheCombinations.get(matcher.getSimpleName());
            for (GumtreeProperties properties : allCombinations) {
                tasks.add(() -> {
                    try {
                        addIntel(getMatcher(matcher.getSimpleName()), properties);
                    } catch (Exception e) {
                        logger.error("Error in task for " + matcher.getSimpleName() + " " + properties, e);
                    }
                    return null;
                });
            }
        }
        try {
            executorService.invokeAll(tasks);
        } catch (Exception e) {
            logger.error(e.getMessage());
            logger.error(e.getCause().toString());
            logger.info("Error in executorService");
        } finally {
            executorService.shutdown();
            if (!executorService.awaitTermination(1, TimeUnit.MINUTES)) {
                String msg = "ExecutorService did not terminate within the timeout.";
                logger.error(msg);
                throw new RuntimeException(msg);
            }
        }
        System.out.println(intels.size() + " intels have been generated");
        return intels;
    }
    private void addIntel(Matcher matcher, GumtreeProperties properties) {
        try {
//        logger.debug("Working on " + matcher + " " + properties);
            Diff diff = makeDiff(matcher, properties);
            BaseDiffComparisonResult compResult = makeStats(diffToASTDiffWithActions(diff, rmDiff.getSrcPath(), rmDiff.getDstPath()));
//        logger.debug("Stats has been generated for " + matcher + " " + properties);
            DiffStats dat = compResult.getDiffStatsList().get(ASTDiffTool.DAT.name());
            if (dat == null)
                throw new RuntimeException("DAT is null");
            NecessaryMappings ignore = compResult.getIgnore().getIntraFileMappings();
            int edSize = diff.editScript.asList().size();

            int edSizeNonJavaDocs = diff.editScript.asList().stream().filter(x ->
                    !isPartOf(x.getNode(), Constants.JAVA_DOC)).toList().size();
//        logger.debug("About to add the intel for " + matcher + " " + properties);
            add(matcher, properties, edSize, edSizeNonJavaDocs, ignore, dat);
        }
        catch (Exception e) {
            logger.error("Error in addIntel", e);
            throw new RuntimeException(e);
        }
    }

    private synchronized void add(Matcher matcher, GumtreeProperties properties, int edSize, int edSizeNonJavaDocs, NecessaryMappings ignore, DiffStats dat) {
        Intel intel = new Intel(info.getRepo(), info.getCommit(), rmDiff.getSrcPath(),
                matcher.getClass().getCanonicalName().replace("com.github.gumtreediff.matchers.CompositeMatchers.", ""), properties.toString(),
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
        try {
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
        catch (Exception e) {
            logger.error("bug3");
            logger.error(properties.toString());
            logger.error(info.makeURL());
            logger.error(rmDiff.getSrcPath());
            logger.error(e.getMessage());
            logger.error("Error in makeDiff", e);
            throw new RuntimeException(e);
        }
    }

    private static ParametersResolvers setParameters() {
        ParametersResolvers domain = new ParametersResolvers();
        domain.getParametersDomain().put(ConfigurationOptions.bu_minsize, new IntParameterDomain(ConfigurationOptions.bu_minsize.name(), Integer.class, 1000, 100, 2000, 100));
        domain.getParametersDomain().put(ConfigurationOptions.bu_minsim, new DoubleParameterDomain(ConfigurationOptions.bu_minsim.name(), Double.class, 0.5, 0.1, 1.0, 0.1));
        domain.getParametersDomain().put(ConfigurationOptions.st_minprio, new IntParameterDomain(ConfigurationOptions.st_minprio.name(), Integer.class, 2, 1, 5, 1));
        domain.getParametersDomain().put(ConfigurationOptions.st_priocalc, new CategoricalParameterDomain(ConfigurationOptions.st_priocalc.name(), String.class, "height", new String[] { "size", "height" }));
        return domain;
    }
    public static void initCacheCombinationProperties(ParametersResolvers domain) throws InstantiationException, IllegalAccessException {
        cacheCombinations.clear();
            for (Class<? extends Matcher> matcher : matchers) {
            Matcher matcherInstance = getMatcher(matcher.getSimpleName());
            List<GumtreeProperties> allCombinations = computesCombinations(matcherInstance, domain);
            cacheCombinations.put(matcher.getSimpleName(), allCombinations);
            logger.info(matcherInstance.getClass().getCanonicalName() + " has " + allCombinations.size() + " combinations");
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

