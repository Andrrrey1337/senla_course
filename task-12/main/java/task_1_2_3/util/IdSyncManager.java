package task_1_2_3.util;

import task_1_2_3.annotations.Component;
import task_1_2_3.annotations.Inject;
import task_1_2_3.annotations.Singleton;
import task_1_2_3.db.ConnectionManager;
import task_1_2_3.exceptions.DaoException;
import task_1_2_3.util.constants.SqlConstants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    private long maxId(String table) throws DaoException {
        String sql = SqlConstants.SQL_MAX_ID_TEMPLATE + table;

        try {
            ConnectionManager cm = ConnectionManager.getInstance();
            Connection conn = cm.getConnection();
            try (Statement st = conn.createStatement();
                 ResultSet rs = st.executeQuery(sql)) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Ошибка чтения MAX(id) из таблицы " + table + ": " + e.getMessage(), e);
        }
    }
}
