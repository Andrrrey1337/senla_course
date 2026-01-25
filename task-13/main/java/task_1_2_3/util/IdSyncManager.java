package task_1_2_3.util;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Inject;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.db.ConnectionManager;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.util.constants.SqlConstants;

@Component
@Singleton
public class IdSyncManager {

    @Inject
    private IdGenerator idGenerator;

    public void sync() {
        long max = 0;
        try {
            max = Math.max(max, maxId(SqlConstants.T_ROOMS));
            max = Math.max(max, maxId(SqlConstants.T_GUESTS));
            max = Math.max(max, maxId(SqlConstants.T_SERVICES));
            max = Math.max(max, maxId(SqlConstants.T_RESIDENCES));
            max = Math.max(max, maxId(SqlConstants.T_SERVICE_RECORDS));

            idGenerator.setNext(max + 1);
            System.out.println("IdGenerator синхронизирован: nextId=" + (max + 1));
        } catch (DaoException e) {
            throw new RuntimeException("Не удалось синхронизировать IdGenerator: " + e.getMessage(), e);
        }
    }

    private long maxId(String entityName) throws DaoException {
        String hql = "select coalesce(max(id), 0) from " + entityName;
        org.hibernate.Session session = ConnectionManager.getInstance().getSession();
        Long result = session.createQuery(hql, Long.class).uniqueResult();
        return result != null ? result : 0;
    }
}
