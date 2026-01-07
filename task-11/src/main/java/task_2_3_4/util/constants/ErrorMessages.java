package task_2_3_4.util.constants;

public final class ErrorMessages {
    private ErrorMessages() {}

    public static final String DB_PROPERTIES_NOT_FOUND = "Файл конфигурации JDBC не найден: ";
    public static final String DB_PROPERTIES_READ_ERROR = "Ошибка чтения конфигурации JDBC: ";
    public static final String JDBC_DRIVER_LOAD_ERROR = "Ошибка загрузки JDBC-драйвера: ";
    public static final String JDBC_CONNECTION_ERROR = "Ошибка подключения к БД: ";

    public static final String TX_BEGIN_ERROR = "Ошибка начала транзакции: ";
    public static final String TX_COMMIT_ERROR = "Ошибка фиксации транзакции: ";
    public static final String TX_ROLLBACK_ERROR = "Ошибка отката транзакции: ";

    public static final String DAO_CREATE_ERROR = "DAO create() ошибка: ";
    public static final String DAO_UPDATE_ERROR = "DAO update() ошибка: ";
    public static final String DAO_DELETE_ERROR = "DAO delete() ошибка: ";
    public static final String DAO_FIND_ERROR = "DAO find() ошибка: ";
}
