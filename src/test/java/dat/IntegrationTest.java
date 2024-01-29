package dat;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import benchmark.utils.PathResolver;
import com.github.gumtreediff.matchers.CompositeMatchers;
import com.github.gumtreediff.matchers.ConfigurationOptions;
import com.github.gumtreediff.matchers.Matcher;
import com.opencsv.CSVWriter;
import fr.gumtree.autotuning.Main;
import fr.gumtree.autotuning.domain.CategoricalParameterDomain;
import fr.gumtree.autotuning.domain.DoubleParameterDomain;
import fr.gumtree.autotuning.domain.IntParameterDomain;
import fr.gumtree.autotuning.searchengines.ExhaustiveEngine;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

import static benchmark.utils.Helpers.runWhatever;
import static fr.gumtree.autotuning.gumtree.ParametersResolvers.defaultDomain;

/* Created by pourya on 2024-01-18*/
public class IntegrationTest {
    private final static Logger logger = LoggerFactory.getLogger(IntegrationTest.class);
    public static void main(String[] args) throws Exception {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        setParameters();
        Configuration configuration = ConfigurationFactory.refOracle();
        Main datTuner = getDatTuner();
        logger.info("Start running DAT");
        int cc = 0;
        for (CaseInfo info : configuration.getAllCases()) {

            cc++;
//            if (!info.getCommit().equals("1a2c1bcdc7267abec9b19d77726aedbb045d79a8")) continue;
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet())
            {
                logger.info("Working on " +  info.makeURL() + " " + rm_astDiff.getSrcPath());
                logger.info("Case " + cc + "/" + configuration.getAllCases().size());
                File srcTempFile = File.createTempFile(PathResolver.fileNameAsFolder(info.getCommit() + "_" + rm_astDiff.getSrcPath()), ".txt");
                    File dstTempFile = File.createTempFile(PathResolver.fileNameAsFolder( info.getCommit() + "_" + rm_astDiff.getDstPath()), ".txt");

                // Write the content to the temporary file
                try (FileWriter fileWriter = new FileWriter(srcTempFile)) {
                    fileWriter.write(projectASTDiff.getFileContentsBefore().get(rm_astDiff.getSrcPath()));
                }
                try (FileWriter fileWriter = new FileWriter(dstTempFile)) {
                    fileWriter.write(projectASTDiff.getFileContentsAfter().get(rm_astDiff.getDstPath()));
                }
                datTuner.setLeft(srcTempFile.getAbsolutePath());
                datTuner.setRight(dstTempFile.getAbsolutePath());

                datTuner.setOut("dat_output/" + info.getRepo() + "/" + info.getCommit());
                datTuner.setFitness(new BenchrmarkFitness(info, projectASTDiff, rm_astDiff, configuration));
                datTuner.call();
                srcTempFile.delete();
                dstTempFile.delete();
            }
            writeIntelListToCsv(BenchrmarkFitness.intels, "intel-" + cc + ".csv");
        }
        logger.info("DAT finished");
        writeIntelListToCsv(BenchrmarkFitness.intels, "intel.csv");
        logger.info("Intel written to csv");
    }

    public static void writeIntelListToCsv(List<Intel> intelList, String filePath) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath))) {
            // Writing header
            String[] header = {
                    "repo", "commit", "srcFile",
                    "Matcher", "Name",
                    "EdSize", "EdSizeNonJavaDoc",
                    "TRV_mappings, TRV_programElements",
                    "TP_mappings, FP_mappings, FN_mappings",
                    "TP_programElements, FP_programElements, FN_programElements",
                    "Precision, Recall, F1"
            };
            writer.writeNext(header);

            // Writing data
            for (Intel intel : intelList) {
                String[] data = {
                        intel.repo, intel.commit,  intel.srcPath,
                        intel.matcher, intel.conf,
                        String.valueOf(intel.edSize),  String.valueOf(intel.edSizeNonJavaDoc),
                        String.valueOf(intel.TRV_mappings), String.valueOf(intel.TRV_programElements),
                        String.valueOf(intel.TP_mappings), String.valueOf(intel.FP_mappings), String.valueOf(intel.FN_mappings),
                        String.valueOf(intel.TP_programElements), String.valueOf(intel.FP_programElements), String.valueOf(intel.FN_programElements),
                        String.valueOf(intel.precision), String.valueOf(intel.recall), String.valueOf(intel.f1)
                };
                writer.writeNext(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private static void setParameters() {
//        defaultDomain.getParametersDomain().put(ConfigurationOptions.bu_minsize, new IntParameterDomain(ConfigurationOptions.bu_minsize.name(), Integer.class, 1000, 100, 2000, 10000));
//        defaultDomain.getParametersDomain().put(ConfigurationOptions.bu_minsim, new DoubleParameterDomain(ConfigurationOptions.bu_minsim.name(), Double.class, 0.5, 0.1, 1.0, 1.0));
//        defaultDomain.getParametersDomain().put(ConfigurationOptions.st_minprio, new IntParameterDomain(ConfigurationOptions.st_minprio.name(), Integer.class, 2, 1, 5, 5));
//        defaultDomain.getParametersDomain().put(ConfigurationOptions.st_priocalc, new CategoricalParameterDomain(ConfigurationOptions.st_priocalc.name(), String.class, "height", new String[] { "size", "height" }));

        //

        defaultDomain.getParametersDomain().put(ConfigurationOptions.bu_minsize, new IntParameterDomain(ConfigurationOptions.bu_minsize.name(), Integer.class, 1000, 100, 2000, 100));
        defaultDomain.getParametersDomain().put(ConfigurationOptions.bu_minsim, new DoubleParameterDomain(ConfigurationOptions.bu_minsim.name(), Double.class, 0.5, 0.1, 1.0, 0.1));
        defaultDomain.getParametersDomain().put(ConfigurationOptions.st_minprio, new IntParameterDomain(ConfigurationOptions.st_minprio.name(), Integer.class, 2, 1, 5, 1));
        defaultDomain.getParametersDomain().put(ConfigurationOptions.st_priocalc, new CategoricalParameterDomain(ConfigurationOptions.st_priocalc.name(), String.class, "height", new String[] { "size", "height" }));
    }


    private static Main getDatTuner() {
        Main datTuner = new Main();
        datTuner.setAstmodel("JDT");
        datTuner.setLeft("examples/src.java");
        datTuner.setRight("examples/dst.java");
        datTuner.setScope("local");
        datTuner.setMode("exhaustive");
        datTuner.setParallel("PROPERTY_LEVEL");
        datTuner.setOut("dat_output");
        datTuner.setNrthreads(5);
        datTuner.setTimeout(5000);
        datTuner.setWriteResults(false);
        Matcher[] matchers = new Matcher[] {
                new CompositeMatchers.SimpleGumtree(),
                new CompositeMatchers.ClassicGumtree(),
                new CompositeMatchers.HybridGumtree(),
        };
        datTuner.setMatchers(matchers);
//        datTuner.setOverwriteresults(true);
        return datTuner;
    }
}

