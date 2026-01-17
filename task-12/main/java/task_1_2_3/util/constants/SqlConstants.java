package task_1_2_3.util.constants;

public final class SqlConstants {
    private SqlConstants() {}

    public static final String T_GUESTS = "guest";
    public static final String T_ROOMS = "room";
    public static final String T_RESIDENCES = "residence";
    public static final String T_SERVICES = "service";
    public static final String T_SERVICE_RECORDS = "servicerecord";

    public static final String SQL_MAX_ID_TEMPLATE = "SELECT COALESCE(MAX(id), 0) FROM ";

    // Общие
    public static final String C_ID = "id";
    public static final String C_NAME = "name";

    // Room
    public static final String C_NUMBER = "number";
    public static final String C_CAPACITY = "capacity";
    public static final String C_STARS = "stars";
    public static final String C_PRICE = "price";
    public static final String C_STATUS = "status";
    public static final String C_CHECK_IN_DATE = "check_in_date";
    public static final String C_CHECK_OUT_DATE = "check_out_date";

    // Residence
    public static final String C_GUEST_ID = "guest_id";
    public static final String C_ROOM_ID = "room_id";
    public static final String C_RES_CHECK_IN_DATE = "check_in_date";
    public static final String C_RES_CHECK_OUT_DATE = "check_out_date";

    // Service
    public static final String C_SERVICE_ID = "id";
    public static final String C_SERVICE_NAME = "name";
    public static final String C_SERVICE_PRICE = "price";

    // ServiceRecord
    public static final String C_SR_GUEST_ID = "guest_id";
    public static final String C_SR_SERVICE_ID = "service_id";
    public static final String C_SR_DATE = "date";
}
