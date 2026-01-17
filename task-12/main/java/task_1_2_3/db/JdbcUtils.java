package task_1_2_3.db;

import java.sql.Date;
import java.time.LocalDate;

public final class JdbcUtils {
    private JdbcUtils() {}

    public static Date toSqlDate(LocalDate date) {
        return date == null ? null : Date.valueOf(date);
    }

    public static LocalDate toLocalDate(Date date) {
        return date == null ? null : date.toLocalDate();
    }
}
