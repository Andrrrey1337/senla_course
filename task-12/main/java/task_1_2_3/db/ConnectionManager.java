package task_1_2_3.db;

import task_1_2_3.exceptions.DaoException;
import task_1_2_3.util.constants.DbConstants;
import task_1_2_3.util.constants.ErrorMessages;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public final class ConnectionManager {
    private static volatile ConnectionManager instance;

    private final Properties properties = new Properties();
    private Connection connection;


    private int txDepth = 0;

    private ConnectionManager() throws DaoException {
        loadProperties();
        initConnection();
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

    public synchronized Connection getConnection() throws DaoException {
        try {
            if (connection == null || connection.isClosed()) {
                initConnection();
            }
            return connection;
        } catch (SQLException e) {
            throw new DaoException(ErrorMessages.JDBC_CONNECTION_ERROR + e.getMessage(), e);
        }
    }

    public synchronized void beginTransaction() throws DaoException {
        try {
            Connection c = getConnection();
            if (txDepth == 0) {
                c.setAutoCommit(false);
            }
            txDepth++;
        } catch (SQLException e) {
            throw new DaoException(ErrorMessages.TX_BEGIN_ERROR + e.getMessage(), e);
        }
    }

    public synchronized void commitTransaction() throws DaoException {
        if (txDepth <= 0) {
            return;
        }
        txDepth--;
        if (txDepth > 0) {
            return;
        }
        try {
            Connection c = getConnection();
            c.commit();
            c.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DaoException(ErrorMessages.TX_COMMIT_ERROR + e.getMessage(), e);
        }
    }

    public synchronized void rollbackTransaction() throws DaoException {
        if (txDepth <= 0) {
            return;
        }
        try {
            Connection c = getConnection();
            c.rollback();
            c.setAutoCommit(true);
        } catch (SQLException e) {
            throw new DaoException(ErrorMessages.TX_ROLLBACK_ERROR + e.getMessage(), e);
        } finally {
            txDepth = 0;
        }
    }

    private void loadProperties() throws DaoException {
        try (InputStream in = ConnectionManager.class.getClassLoader().getResourceAsStream(DbConstants.PROPERTIES_FILE)) {
            if (in == null) {
                throw new DaoException(ErrorMessages.DB_PROPERTIES_NOT_FOUND + DbConstants.PROPERTIES_FILE);
            }
            properties.load(in);
        } catch (DaoException e) {
            throw e;
        } catch (Exception e) {
            throw new DaoException(ErrorMessages.DB_PROPERTIES_READ_ERROR + e.getMessage(), e);
        }
    }

    private void initConnection() throws DaoException {
        try {
            final String driver = properties.getProperty(DbConstants.PROP_DRIVER);
            if (driver != null && !driver.isBlank()) {
                Class.forName(driver);
            }

            final String url = properties.getProperty(DbConstants.PROP_URL);
            final String user = properties.getProperty(DbConstants.PROP_USER);
            final String password = properties.getProperty(DbConstants.PROP_PASSWORD);

            connection = DriverManager.getConnection(url, user, password);
            connection.setAutoCommit(true);
            txDepth = 0;
        } catch (ClassNotFoundException e) {
            throw new DaoException(ErrorMessages.JDBC_DRIVER_LOAD_ERROR + e.getMessage(), e);
        } catch (Exception e) {
            throw new DaoException(ErrorMessages.JDBC_CONNECTION_ERROR + e.getMessage(), e);
        }
    }
}
