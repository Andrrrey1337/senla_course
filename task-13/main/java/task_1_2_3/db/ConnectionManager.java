package task_1_2_3.db;

import task_1_2_3.exceptions.DaoException;
import task_1_2_3.util.constants.DbConstants;
import task_1_2_3.util.constants.ErrorMessages;
import task_1_2_3.model.*;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;


public final class ConnectionManager {
    private static volatile ConnectionManager instance;
    private SessionFactory sessionFactory;
    private final ThreadLocal<Session> threadLocalSession = new ThreadLocal<>();
    private final ThreadLocal<Transaction> threadLocalTransaction = new ThreadLocal<>();

    private ConnectionManager() throws DaoException {
        initSessionFactory();
    }

    public static ConnectionManager getInstance() throws DaoException {
        if (instance == null) {
            synchronized (ConnectionManager.class) {
                if (instance == null) {
                    instance = new ConnectionManager();
                }
            }
        }
        return instance;
    }

    private void initSessionFactory() throws DaoException {
        try {
            Properties dbProps = new Properties();
            try (InputStream in = ConnectionManager.class.getClassLoader().getResourceAsStream(DbConstants.PROPERTIES_FILE)) {
                if (in == null) throw new DaoException(ErrorMessages.DB_PROPERTIES_NOT_FOUND);
                dbProps.load(in);
            }

            Configuration configuration = new Configuration();
            // настройки Hibernate
            configuration.setProperty("hibernate.connection.driver_class", dbProps.getProperty(DbConstants.PROP_DRIVER));
            configuration.setProperty("hibernate.connection.url", dbProps.getProperty(DbConstants.PROP_URL));
            configuration.setProperty("hibernate.connection.username", dbProps.getProperty(DbConstants.PROP_USER));
            configuration.setProperty("hibernate.connection.password", dbProps.getProperty(DbConstants.PROP_PASSWORD));

            configuration.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            //configuration.setProperty("hibernate.show_sql", "true");
            configuration.setProperty("hibernate.format_sql", "true");
            configuration.setProperty("hibernate.current_session_context_class", "thread");

            // регистрация сущностей
            configuration.addAnnotatedClass(Guest.class);
            configuration.addAnnotatedClass(Room.class);
            configuration.addAnnotatedClass(Service.class);
            configuration.addAnnotatedClass(Residence.class);
            configuration.addAnnotatedClass(ServiceRecord.class);

            sessionFactory = configuration.buildSessionFactory();
        } catch (Exception e) {
            throw new DaoException(ErrorMessages.JDBC_CONNECTION_ERROR + e.getMessage(), e);
        }
    }

    public Session getSession() {
        Session session = threadLocalSession.get();
        if (session == null || !session.isOpen()) {
            session = sessionFactory.openSession();
            threadLocalSession.set(session);
        }
        return session;
    }

    private void closeSession() {
        Session session = threadLocalSession.get();
        if (session != null && session.isOpen()) {
            session.close();
        }
        threadLocalSession.remove();
    }

    public void beginTransaction() {
        Session session = getSession();
        if (threadLocalTransaction.get() == null || !threadLocalTransaction.get().isActive()) {
            Transaction tx = session.beginTransaction();
            threadLocalTransaction.set(tx);
        }
    }

    public void commitTransaction() throws DaoException {
        Transaction tx = threadLocalTransaction.get();
        if (tx != null && tx.isActive()) {
            try {
                tx.commit();
                threadLocalTransaction.remove();
            } catch (Exception e) {
                throw new DaoException(ErrorMessages.TX_COMMIT_ERROR + e.getMessage(), e);
            } finally {
                closeSession();
            }
        }
    }

    public void rollbackTransaction() {
        Transaction tx = threadLocalTransaction.get();
        if (tx != null && tx.isActive()) {
            try {
                tx.rollback();
            } catch (Exception e) {
                System.err.println(ErrorMessages.TX_ROLLBACK_ERROR + e.getMessage());
            } finally {
                threadLocalTransaction.remove();
                closeSession();
            }
        }
    }
}
