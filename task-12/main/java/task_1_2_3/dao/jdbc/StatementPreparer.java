package task_1_2_3.dao.jdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

@FunctionalInterface
public interface StatementPreparer {
    void accept(PreparedStatement ps) throws SQLException;
}
