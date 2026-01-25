package task_1_2_3.util.constants;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String DB_PROPERTIES_NOT_FOUND = "Файл конфигурации JDBC не найден: ";
    public static final String JDBC_CONNECTION_ERROR = "Ошибка подключения к БД: ";

    public static final String TX_COMMIT_ERROR = "Ошибка фиксации транзакции: ";
    public static final String TX_ROLLBACK_ERROR = "Ошибка отката транзакции: ";
}
