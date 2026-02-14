package task.util.constants;

public final class BusinessMessages {
    private BusinessMessages() { }

    public static final String GUEST_NOT_FOUND_PREFIX = "Гость с именем ";
    public static final String GUEST_NOT_FOUND_SUFFIX = " не найден";

    public static final String GUEST_NAME_EMPTY = "Имя гостя не может быть пустым";
    public static final String SERVICE_NAME_EMPTY = "Название услуги не может быть пустым";

    public static final String PRICE_NEGATIVE = "Цена не может быть отрицательной";

    public static final String ROOM_NOT_FOUND_PREFIX = "Комната с номером ";
    public static final String ROOM_NOT_FOUND_SUFFIX = " не найдена";

    public static final String ROOM_NOT_AVAILABLE_PREFIX = "Комната с номером ";
    public static final String ROOM_NOT_AVAILABLE_SUFFIX = " недоступна для заселения (статус: ";

    public static final String CHECKIN_AFTER_CHECKOUT = "Дата заселения не может быть позже даты выселения";
    public static final String RESIDENCE_CHECKIN_AFTER_CHECKOUT = "Дата заезда не может быть позже даты выезда";

    public static final String ROOM_ALREADY_EXISTS_PREFIX = "Комната с номером ";
    public static final String ROOM_ALREADY_EXISTS_SUFFIX = " уже существует";

    public static final String ROOM_STATUS_CHANGE_DISABLED = "Изменение статуса комнаты отключено настройками";

    public static final String ROOM_NOT_OCCUPIED_PREFIX = "Комната с номером ";
    public static final String ROOM_NOT_OCCUPIED_SUFFIX = " не занята, невозможно выполнить выселение";

    public static final String SERVICE_NOT_FOUND_PREFIX = "Услуга с названием '";
    public static final String SERVICE_NOT_FOUND_SUFFIX = "' не найдена";
}
