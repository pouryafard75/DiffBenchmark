package dat;

import benchmark.data.diffcase.IBenchmarkCase;
import benchmark.data.exp.ExperimentsEnum;
import benchmark.data.exp.IExperiment;
import org.hibernate.Session;
import org.refactoringminer.astDiff.models.ASTDiff;
import org.refactoringminer.astDiff.models.ProjectASTDiff;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/* Created by pourya on 2024-01-18*/
public class MakeIntel {
    private final static Logger logger = LoggerFactory.getLogger(MakeIntel.class);
    private static final IExperiment experiment = ExperimentsEnum.D4J_EXP;
    private static final String destination = "intel.csv";
    private static int numThreads = Runtime.getRuntime().availableProcessors();

    public static void main(String[] args) throws Exception {
        logger.info("Start running DAT");

        int case_count = 0;
        IntelDao intelDao = new IntelDao();
        try {
            Session session = HibernateUtil.getSessionFactory().openSession();
            System.out.println("aaa");
            if (true) return;
            for (IBenchmarkCase info : experiment.getDataset().getCases()) {
                case_count++;
                List<Intel> intels = new ArrayList<>();
                ProjectASTDiff projectASTDiff = info.getProjectASTDiff();
                for (ASTDiff rm_astDiff : projectASTDiff.getDiffSet()) {
                    logger.info("Working on " + info.getID() + " " + rm_astDiff.getSrcPath());
                    logger.info("Case " + case_count + "/" + experiment.getDataset().getCases().size());
                    IntelGenerator dat = new IntelGenerator(info, projectASTDiff, rm_astDiff, experiment);
                    intels.addAll(dat.run(numThreads));
                }
                intelDao.insertIntels(intels);
                 if (case_count > 2) break; //TODO: REMOVE THIS
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

