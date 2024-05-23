package dat;

import benchmark.utils.CaseInfo;
import benchmark.utils.Configuration.Configuration;
import benchmark.utils.Configuration.ConfigurationFactory;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static benchmark.utils.Helpers.runWhatever;
import static dat.Intel.writeIntelListToCsv;

/* Created by pourya on 2024-01-18*/
public class Make {
    private final static Logger logger = LoggerFactory.getLogger(Make.class);
    private static final Configuration configuration = ConfigurationFactory.refOracle();
    private static final String destination = "intel.csv";
    private static int numThreads = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {
        logger.info("Start running DAT");
        int case_count = 0;
        List<Intel> intels = new ArrayList<>();
        try {
            for (CaseInfo info : configuration.getAllCases()) {
                case_count++;
                ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
                for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet()) {
//                    logger.info("Working on " + info.makeURL() + " " + rm_astDiff.getSrcPath());
                    logger.info("Case " + case_count + "/" + configuration.getAllCases().size());
                    GridSearch dat = new GridSearch(info, projectASTDiff, rm_astDiff, configuration);
                    intels.addAll(dat.run(numThreads));
                }
                if (case_count == 5) break;
                writeIntelListToCsv(intels, destination);
                logger.info("Intel written to csv");
            }
        }
        catch (Exception e) {
            logger.error("Error in DAT", e);
            logger.debug("Error in DAT", e);
            throw new RuntimeException(e);
        }
        writeIntelListToCsv(intels, destination);
        logger.info("DAT finished");
    }
}

