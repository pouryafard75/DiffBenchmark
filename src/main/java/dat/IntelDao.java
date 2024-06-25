package dat;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.util.List;

public class IntelDao {
    private final static Logger logger = LoggerFactory.getLogger(IntelDao.class);

    public synchronized void insertIntels(List<Intel> intels) {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;

        try {
            transaction = session.beginTransaction();

            for (Intel intel : intels) {
                session.save(intel);
            }

            transaction.commit();
            logger.info("Batch insertion of intels completed successfully!");
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
}
