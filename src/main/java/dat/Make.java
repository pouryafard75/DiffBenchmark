package dat;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.refactoringminer.astDiff.actions.ASTDiff;
import org.refactoringminer.astDiff.actions.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static benchmark.utils.Helpers.runWhatever;
import static dat.Intel.writeIntelListToCsv;

/* Created by pourya on 2024-01-18*/
public class Make {
    private final static Logger logger = LoggerFactory.getLogger(Make.class);
    private static final Configuration configuration = ConfigurationFactory.refOracle();
    private static int numThreads = 15;

    public static void main(String[] args) throws Exception {
        logger.info("Start running DAT");
        int case_count = 1;
        for (CaseInfo info : configuration.getAllCases()) {
            case_count++;
            ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
            for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet())
            {
                logger.info("Working on " +  info.makeURL() + " " + rm_astDiff.getSrcPath());
                logger.info("Case " + case_count + "/" + configuration.getAllCases().size());
                GridSearch dat = new GridSearch(info, projectASTDiff, rm_astDiff, configuration);
                dat.run(numThreads);
            }
            if (case_count % 10 == 0) {
                writeIntelListToCsv(BenchrmarkFitness.intels, "intels" + case_count + ".csv");
            }
        }
        logger.info("DAT finished");
        writeIntelListToCsv(BenchrmarkFitness.intels, "intel.csv");
        logger.info("Intel written to csv");
    }
}

