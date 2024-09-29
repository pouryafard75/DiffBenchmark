package dat;

import benchmark.data.diffcase.BenchmarkCase;
import benchmark.data.exp.EExperiment;
import benchmark.data.exp.ExperimentConfiguration;
import benchmark.data.exp.IExperiment;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static benchmark.utils.Helpers.runWhatever;

/* Created by pourya on 2024-01-18*/
public class MakeIntels {
    private final static Logger logger = LoggerFactory.getLogger(MakeIntels.class);
    private static final IExperiment experiment = EExperiment.REF_EXP_3_0;
    private static final String destination = "intel.csv";
    private static int numThreads = Runtime.getRuntime().availableProcessors();


    public static void main(String[] args) throws Exception {
        logger.info("Start running DAT");
        int case_count = 0;
        IntelDao intelDao = new IntelDao();
        try {
            for (BenchmarkCase info : experiment.getDataset().getCases()) {
                case_count++;
                List<Intel> intels = new ArrayList<>();
                ProjectASTDiff projectASTDiff = runWhatever(info.getRepo(), info.getCommit());
                for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet()) {
//                    logger.info("Working on " + info.makeURL() + " " + rm_astDiff.getSrcPath());
                    logger.info("Case " + case_count + "/" + experiment.getDataset().getCases().size());
                    GridSearch dat = new GridSearch(info, projectASTDiff, rm_astDiff, experiment);
                    intels.addAll(dat.run(numThreads));
                }
                intelDao.insertIntels(intels);
                // if (case_count == 2) break;
            }
        }
        catch (Exception e) {
            logger.error("Error in DAT", e);
            logger.debug("Error in DAT", e);
            throw new RuntimeException(e);
        }
        finally {
            HibernateUtil.shutdown();
        }
        logger.info("DAT finished");

    }
}

