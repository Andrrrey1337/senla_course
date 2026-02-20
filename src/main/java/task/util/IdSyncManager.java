package task.util;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import task.util.constants.SqlConstants;

@Component
public class IdSyncManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdSyncManager.class);

    private final IdGenerator idGenerator;
    private final SessionFactory sessionFactory;

    public IdSyncManager(IdGenerator idGenerator, SessionFactory sessionFactory) {
        this.idGenerator = idGenerator;
        this.sessionFactory = sessionFactory;
    }

    @EventListener(ContextRefreshedEvent.class)
    @Transactional(readOnly = true)
    public void sync() {
        long max = 0;
        try {
            max = Math.max(max, maxId(SqlConstants.T_ROOMS));
            max = Math.max(max, maxId(SqlConstants.T_GUESTS));
            max = Math.max(max, maxId(SqlConstants.T_SERVICES));
            max = Math.max(max, maxId(SqlConstants.T_RESIDENCES));
            max = Math.max(max, maxId(SqlConstants.T_SERVICE_RECORDS));

            idGenerator.setNext(max + 1);
            LOGGER.info("IdGenerator синхронизирован при запуске: nextId={}", (max + 1));
        } catch (Exception e) {
            LOGGER.error("Ошибка при синхронизации IdGenerator", e);
            throw new RuntimeException("Не удалось синхронизировать IdGenerator: " + e.getMessage(), e);
        }
    }

    private long maxId(String entityName) {
        String hql = "select coalesce(max(id), 0) from " + entityName;
        Session session = sessionFactory.getCurrentSession();
        Long result = session.createQuery(hql, Long.class).uniqueResult();
        return result != null ? result : 0;
    }
}